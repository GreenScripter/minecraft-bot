package greenscripter.minecraft;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;

import greenscripter.minecraft.play.handler.PlayHandler;

public class AsyncSwarmController {

	private static int nextID = 0;
	private static int nextBotID = 0;

	public int id = nextID++;
	public boolean bungeeMode = false;
	public String serverIP = "localhost";
	public int serverPort = 25565;

	private List<ServerConnection> remove = new ArrayList<>();
	private List<ServerConnection> next = new ArrayList<>();
	private List<ServerConnection> connections = new ArrayList<>();
	private List<ServerConnection> dead = new ArrayList<>();
	private List<ServerConnection> extract = new ArrayList<>();

	public List<PlayHandler> globalHandlers = new ArrayList<>();
	public Function<ServerConnection, List<PlayHandler>> localHandlers = f -> List.of();

	public Function<Integer, String> botNames = id -> "bot" + id;
	public Function<String, UUID> namesToUUIDs = name -> UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
	public Supplier<Integer> botIds = () -> nextBotID++;
	public Consumer<ServerConnection> deathCallback = null;
	public Consumer<ServerConnection> joinCallback = null;
	public Runnable tickCallback = null;

	public ServerConnection ticking = null;

	Thread tickThread;

	public AsyncSwarmController(String ip, int port, List<PlayHandler> globalHandlers) {
		this.serverIP = ip;
		this.serverPort = port;
		this.globalHandlers.addAll(globalHandlers);
	}

	public void connect(int botCount, long joinDelay) {
		for (int i = 0; i < botCount; i++) {
			int c = botIds.get();
			new Thread(() -> {
				String name = botNames.apply(c);
				UUID uuid = namesToUUIDs.apply(name);
				ServerConnection sc = new ServerConnection(serverIP, serverPort, name, uuid, globalHandlers);
				try {
					sc.connect();
					localHandlers.apply(sc).forEach(sc::addPlayHandler);
					sc.owner = Thread.currentThread();
					sc.id = c;
					sc.bungeeMode = bungeeMode;
					while (true) {
						sc.step();
						if (sc.connectionState.equals(ServerConnection.ConnectionState.PLAY)) {
							synchronized (next) {
								sc.owner = null;
								next.add(sc);
							}
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					synchronized (dead) {
						dead.add(sc);
					}
				}
			}).start();
			try {
				Thread.sleep(joinDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void reconnectDead(List<ServerConnection> dead, long joinDelay) {
		reconnectDead(dead, joinDelay, false);
	}

	public void reconnectDead(List<ServerConnection> dead, long joinDelay, boolean wait) {
		for (ServerConnection serverConn : dead) {
			ServerConnection sc = serverConn;
			Thread t = new Thread(() -> {
				try {
					sc.connect();
					sc.owner = Thread.currentThread();
					while (true) {
						sc.step();
						if (sc.connectionState.equals(ServerConnection.ConnectionState.PLAY)) {
							synchronized (next) {
								sc.owner = null;
								next.add(sc);
							}
							break;
						}
					}
				} catch (Throwable e) {
					e.printStackTrace();
					synchronized (this.dead) {
						this.dead.add(sc);
					}
				}
			});
			t.setName("ReconnectDead");
			t.start();
			try {
				if (wait) t.join();
				Thread.sleep(joinDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Get a copy of the live connection pool.
	 */
	public List<ServerConnection> getAlive() {
		synchronized (connections) {
			return new ArrayList<>(connections);
		}
	}

	/**
	 * Get a copy of the dead connection pool.
	 */
	public List<ServerConnection> getDead() {
		synchronized (dead) {
			return new ArrayList<>(dead);
		}
	}

	/**
	 * Take connections out of the dead pool.
	 */
	public List<ServerConnection> takeDead(List<ServerConnection> toRemove) {
		List<ServerConnection> removed = new ArrayList<>();
		synchronized (dead) {
			for (ServerConnection sc : toRemove) {
				if (dead.remove(sc)) {
					removed.add(sc);
				} else {
					System.out.println("Failed to remove bot " + sc + "!!!!!");
				}
			}
		}
		return removed;
	}

	public void grantDead(List<ServerConnection> toRemove) {
		synchronized (dead) {
			dead.addAll(toRemove);
		}
	}

	/**
	 * Grant a set of new connections, they will be ticked on next tick if they are given up by
	 * their current owner.
	 */
	public void grantAlive(List<ServerConnection> toAdd) {
		synchronized (next) {
			next.addAll(toAdd);
		}
	}

	/**
	 * ServerConnections are scheduled for removal, and can safely be granted to another Controller.
	 * May still get ticked once more by this one.
	 */
	public void takeAlive(List<ServerConnection> toRemove) {
		synchronized (extract) {
			extract.removeAll(toRemove);
		}
	}

	public void start() {
		tickThread = new Thread(() -> {
			try {
				Selector selector = Selector.open();

				long lastLog = System.currentTimeMillis();
				long max = 0;
				long min = Integer.MAX_VALUE;
				int steps = 0;
				long timeSkipped = 0;
				long packets = 0;
				Map<SelectionKey, ServerConnection> keyMapping = new IdentityHashMap<>();
				Map<ServerConnection, SelectionKey> connectionMapping = new IdentityHashMap<>();
				while (true) {
					long start = System.currentTimeMillis();
					if (!next.isEmpty()) synchronized (next) {
						synchronized (connections) {
							next.removeIf(sc -> {
								ticking = sc;
								try {
									if (sc.owner == null) {
										sc.owner = Thread.currentThread();
										if (joinCallback != null) try {
											joinCallback.accept(sc);
										} catch (Exception e) {
											e.printStackTrace();
										}
										try {
											sc.channel.configureBlocking(false);
											SelectionKey sk = sc.channel.register(selector, SelectionKey.OP_READ);
											keyMapping.put(sk, sc);
											connectionMapping.put(sc, sk);
										} catch (ClosedChannelException e) {
											e.printStackTrace();
										}
										connections.add(sc);
										return true;
									} else {
										return false;
									}
								} catch (Exception e) {
									e.printStackTrace();
									return false;
								}
							});
							ticking = null;
							connections.sort(Comparator.comparing(c -> c.id));
						}
					}
					//connections only needs to be synchronized during writes, 
					//other threads are not allowed to write, only read.
					ArrayList<ServerConnection> copy = new ArrayList<>(connections);

					for (ServerConnection sc : copy) {
						if (sc.connectionState == ServerConnection.ConnectionState.DISCONNECTED) {
							if (!remove.contains(sc)) remove.add(sc);
							continue;
						}
						ticking = sc;
						try {
							synchronized (sc) {
								sc.step();

								while (!sc.waiting()) {
									sc.step();
								}

								sc.tick();
								sc.out.flush();
							}

						} catch (Exception e) {
							e.printStackTrace();
							if (!remove.contains(sc)) remove.add(sc);
							var removed = connectionMapping.remove(sc);
							if (removed != null) {
								keyMapping.remove(removed);
								removed.cancel();
							}
							sc.connectionState = ServerConnection.ConnectionState.DISCONNECTED;
							try {
								sc.channel.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
						packets += (sc.in.packetCounter);
						sc.in.packetCounter = 0;
					}
					ticking = null;

					if (!remove.isEmpty()) synchronized (remove) {
						synchronized (extract) {
							remove.removeAll(extract);
						}
						remove.forEach(sc -> {
							sc.owner = null;
							var removed = connectionMapping.remove(sc);
							if (removed != null) {
								keyMapping.remove(removed);
								removed.cancel();
							}
						});
						synchronized (connections) {
							connections.removeAll(remove);
						}
						synchronized (dead) {
							dead.addAll(remove);
							if (deathCallback != null) {
								for (ServerConnection sc : remove) {
									try {
										deathCallback.accept(sc);
									} catch (Exception e) {
										e.printStackTrace();
									}
									for (PlayHandler handler : sc.getPlayHandlers()) {
										try {
											handler.handleDisconnect(sc);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}

								}
							}
						}

						remove.clear();
					}
					if (!extract.isEmpty()) synchronized (extract) {
						synchronized (connections) {
							connections.removeAll(extract);
						}
						extract.forEach(sc -> {
							sc.owner = null;
							var removed = connectionMapping.remove(sc);
							if (removed != null) {
								keyMapping.remove(removed);
								removed.cancel();
							}
						});
						extract.clear();
					}

					if (tickCallback != null) try {
						tickCallback.run();
					} catch (Exception e) {
						e.printStackTrace();
					}

					long duration = System.currentTimeMillis() - start;
					max = Math.max(max, duration);
					min = Math.min(min, duration);
					steps++;
					if (System.currentTimeMillis() - lastLog > 1000) {
						System.out.println("Servicing all clients took " + (duration) + " ms. min " + min + " max " + max + " average " + (System.currentTimeMillis() - lastLog - timeSkipped) / steps + " packets " + packets);
						packets = 0;
						lastLog = System.currentTimeMillis();
						max = 0;
						min = Integer.MAX_VALUE;
						steps = 0;
						timeSkipped = 0;
					}
					timeSkipped += 50 - duration;

					while (System.currentTimeMillis() - start < 50) {
						try {
							long wait = 50 - (System.currentTimeMillis() - start);
							if (wait <= 0) break;
							selector.select(wait);
							var keys = selector.selectedKeys();
							for (var key : keys) {
								ServerConnection sc = keyMapping.get(key);
								if (sc == null) continue;
								ticking = sc;
								try {
									synchronized (sc) {
										sc.step();

										while (!sc.waiting()) {
											sc.step();
										}
										sc.out.flush();
									}
								} catch (Exception e) {
									e.printStackTrace();
									if (!remove.contains(sc)) remove.add(sc);
									keyMapping.remove(key);
									connectionMapping.remove(sc);
									key.cancel();
									sc.connectionState = ServerConnection.ConnectionState.DISCONNECTED;
									try {
										sc.channel.close();
									} catch (IOException e1) {
										e1.printStackTrace();
									}
								}
								packets += (sc.in.packetCounter);
								sc.in.packetCounter = 0;
							}
							ticking = null;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		tickThread.setName("AsyncSwarmController-" + id);
		tickThread.start();
	}

}
