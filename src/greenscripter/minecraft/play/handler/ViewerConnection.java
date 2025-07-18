package greenscripter.minecraft.play.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

import java.io.IOException;
import java.net.Socket;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.ServerConnection.ConnectionState;
import greenscripter.minecraft.gameinfo.Registries;
import greenscripter.minecraft.nbt.NBTTagCompound;
import greenscripter.minecraft.nbt.NBTTagString;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.configuration.AckFinishConfigPacket;
import greenscripter.minecraft.packet.c2s.configuration.ClientKnownPacksConfigPacket;
import greenscripter.minecraft.packet.c2s.handshake.HandshakePacket;
import greenscripter.minecraft.packet.c2s.login.LoginAcknowledgePacket;
import greenscripter.minecraft.packet.c2s.login.LoginStartPacket;
import greenscripter.minecraft.packet.c2s.play.AckChunksPacket;
import greenscripter.minecraft.packet.c2s.play.ExecuteCommandPacket;
import greenscripter.minecraft.packet.c2s.play.ExecuteCommandSignedPacket;
import greenscripter.minecraft.packet.c2s.play.KeepAliveReplyPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerActionPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionRotationPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMoveRotationPacket;
import greenscripter.minecraft.packet.c2s.play.TeleportConfirmPacket;
import greenscripter.minecraft.packet.c2s.play.UseItemOnPacket;
import greenscripter.minecraft.packet.c2s.play.inventory.ClickContainerPacket;
import greenscripter.minecraft.packet.c2s.play.inventory.CloseContainerPacket;
import greenscripter.minecraft.packet.c2s.play.inventory.HotbarSlotPacket;
import greenscripter.minecraft.packet.c2s.status.PingRequestPacket;
import greenscripter.minecraft.packet.c2s.status.StatusRequestPacket;
import greenscripter.minecraft.packet.s2c.configuration.DisconnectConfigPacket;
import greenscripter.minecraft.packet.s2c.configuration.FinishConfigPacket;
import greenscripter.minecraft.packet.s2c.configuration.RegistryConfigPacket;
import greenscripter.minecraft.packet.s2c.configuration.ServerKnownPacksConfigPacket;
import greenscripter.minecraft.packet.s2c.login.DisconnectLoginPacket;
import greenscripter.minecraft.packet.s2c.login.LoginSuccessPacket;
import greenscripter.minecraft.packet.s2c.login.SetCompressionPacket;
import greenscripter.minecraft.packet.s2c.play.AckBlockChangePacket;
import greenscripter.minecraft.packet.s2c.play.DisconnectPacket;
import greenscripter.minecraft.packet.s2c.play.GameEventPacket;
import greenscripter.minecraft.packet.s2c.play.KeepAlivePacket;
import greenscripter.minecraft.packet.s2c.play.PlayerInfoRemovePacket;
import greenscripter.minecraft.packet.s2c.play.PlayerInfoUpdatePacket;
import greenscripter.minecraft.packet.s2c.play.SystemChatPacket;
import greenscripter.minecraft.packet.s2c.play.blocks.BlockUpdatePacket;
import greenscripter.minecraft.packet.s2c.play.blocks.ChunkBatchFinishPacket;
import greenscripter.minecraft.packet.s2c.play.blocks.ChunkBatchStartPacket;
import greenscripter.minecraft.packet.s2c.play.blocks.ChunkDataPacket;
import greenscripter.minecraft.packet.s2c.play.blocks.SetChunkCenterPacket;
import greenscripter.minecraft.packet.s2c.play.entity.EntityEquipmentPacket;
import greenscripter.minecraft.packet.s2c.play.entity.EntityMetaDataPacket;
import greenscripter.minecraft.packet.s2c.play.entity.EntitySpawnPacket;
import greenscripter.minecraft.packet.s2c.play.entity.XPOrbSpawnPacket;
import greenscripter.minecraft.packet.s2c.play.inventory.OpenContainerPacket;
import greenscripter.minecraft.packet.s2c.play.inventory.SetContainerContentPacket;
import greenscripter.minecraft.packet.s2c.play.inventory.SetHeldItemPacket;
import greenscripter.minecraft.packet.s2c.play.self.RespawnPacket;
import greenscripter.minecraft.packet.s2c.play.self.SetHealthPacket;
import greenscripter.minecraft.packet.s2c.play.self.TeleportRequestPacket;
import greenscripter.minecraft.packet.s2c.status.PingResponsePacket;
import greenscripter.minecraft.packet.s2c.status.StatusResponsePacket;
import greenscripter.minecraft.play.data.InventoryData;
import greenscripter.minecraft.play.data.PlayerData;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.RegistryData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.handler.ViewerTrackPlayHandler.ViewerTrackPlayData;
import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.utils.Vector;
import greenscripter.minecraft.world.Chunk;
import greenscripter.minecraft.world.ChunkDataEncoder;
import greenscripter.minecraft.world.entity.Entity;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class ViewerConnection extends PlayHandler {

	Socket client;
	MCInputStream clientIn;
	MCOutputStream clientOut;
	public String requestedName;
	public UUID requestedUUID;

	public ServerConnection linked;
	public List<ServerConnection> secondaryLinks = Collections.synchronizedList(new ArrayList<>());

	List<Integer> handlesPackets;
	public ConnectionState connectionState = ConnectionState.HANDSHAKE;

	Set<Integer> awaitingTps = new HashSet<>();

	public Consumer<ViewerConnection> loggedInCallback;

	public Supplier<String> pingResponse;

	public List<BiPredicate<ViewerConnection, String>> commandHandlers = new ArrayList<>();

	public int viewMode;

	public PositionData clientPos = new PositionData();
	public boolean compress = true;

	public static final int FORCE_LOOK = 0b1;
	public static final int FORCE_MOVE = 0b10;
	public static final int FORCE_BLOCKS = 0b100;
	public static final int FORCE_INVENTORY = 0b1000;
	public static final int FORCE_BLOCK_OUTGOING = 0b10000;

	public static final int FORCE_DEFAULT = FORCE_MOVE | FORCE_BLOCKS | FORCE_INVENTORY | FORCE_BLOCK_OUTGOING;

	public ViewerConnection(ServerConnection linkTo, Socket client) throws IOException {
		handlesPackets = new ArrayList<>();
		for (int i = 0; i < 200; i++) {
			handlesPackets.add(i);
		}
		this.linked = linkTo;

		this.client = client;
		client.setSoTimeout(20000);
		clientIn = new MCInputStream(client.getInputStream());
		clientOut = new MCOutputStream(client.getOutputStream());
	}

	boolean started = false;

	private boolean updatePositionFor(UnknownPacket p, PositionData pos) {
		if (p.id == PlayerMovePacket.packetId) {
			PlayerMovePacket move = p.convert(new PlayerMovePacket());

			pos.onGround = move.onGround;
			return true;
		}

		if (p.id == PlayerMovePositionPacket.packetId) {
			PlayerMovePositionPacket move = p.convert(new PlayerMovePositionPacket());

			pos.onGround = move.onGround;
			pos.pos.x = move.x;
			pos.pos.y = move.y;
			pos.pos.z = move.z;
			return true;
		}

		if (p.id == PlayerMoveRotationPacket.packetId) {
			PlayerMoveRotationPacket move = p.convert(new PlayerMoveRotationPacket());

			pos.onGround = move.onGround;
			pos.pitch = move.pitch;
			pos.yaw = move.yaw;
			return true;
		}

		if (p.id == PlayerMovePositionRotationPacket.packetId) {
			PlayerMovePositionRotationPacket move = p.convert(new PlayerMovePositionRotationPacket());

			pos.onGround = move.onGround;
			pos.pitch = move.pitch;
			pos.yaw = move.yaw;
			pos.pos.x = move.x;
			pos.pos.y = move.y;
			pos.pos.z = move.z;
			return true;
		}
		return false;
	}

	public void start() {
		if (started) return;
		started = true;

		new Thread(() -> {
			try {
				while (true) {
					UnknownPacket p = clientIn.readGeneralPacket();
					//					System.out.println(connectionState + " " + PacketIds.getC2SPacketName(connectionState.name, p.id));
					switch (connectionState) {
						case HANDSHAKE:
							if (p.id == HandshakePacket.packetId) {
								HandshakePacket handshake = p.convert(new HandshakePacket());
								if (handshake.nextState == 2) {
									System.out.println("Logging in");
									connectionState = ConnectionState.LOGIN;
								}
								if (handshake.nextState == 1) {
									System.out.println("Querying");
									connectionState = ConnectionState.STATUS;
									client.setSoTimeout(10000);
								}
							}
							break;
						case STATUS:
							if (p.id == StatusRequestPacket.packetId) {
								StatusResponsePacket resp = new StatusResponsePacket();
								resp.value = pingResponse == null ? "{}" : pingResponse.get();
								clientOut.writePacket(resp);
							}
							if (p.id == PingRequestPacket.packetId) {
								PingRequestPacket req = p.convert(new PingRequestPacket());
								clientOut.writePacket(new PingResponsePacket(req.value));
								client.close();
								return;
							}
							break;
						case LOGIN:
							if (p.id == LoginStartPacket.packetId) {
								LoginStartPacket loginStart = p.convert(new LoginStartPacket());
								requestedName = loginStart.name.replaceAll("[^0-9a-zA-Z]", "");
								while (requestedName.length() < 3) {
									requestedName += "_";
								}
								if (requestedName.length() > 16) {
									requestedName = requestedName.substring(0, 16);
								}
								requestedUUID = loginStart.uuid;

								SetCompressionPacket compress = new SetCompressionPacket();
								compress.value = 256;
								clientOut.writePacket(compress);

								clientIn.compression = true;
								clientOut.compressionThreshold = compress.value;

								clientOut.actuallyCompress = this.compress;

								LoginSuccessPacket login = new LoginSuccessPacket();
								login.name = requestedName;
								login.uuid = UUID.randomUUID();
								login.properties = 0;
								clientOut.writePacket(login);
								System.out.println("Logged in " + login.name);

								if (loggedInCallback != null) {
									loggedInCallback.accept(this);
								}
							}
							if (p.id == LoginAcknowledgePacket.packetId) {
								connectionState = ConnectionState.CONFIGURATION;
								clientOut.writePacket(new ServerKnownPacksConfigPacket());
								if (linked == null) {
									writePacket(new DisconnectConfigPacket(new NBTTagString("No bot to control.")));
									kick();
								}
							}
							break;
						case CONFIGURATION:
							if (p.id == ClientKnownPacksConfigPacket.packetId) {
								RegistryData registryData = linked.getData(RegistryData.class);

								for (var registry : registryData.registries.values()) {
									clientOut.writePacket(new RegistryConfigPacket(registry));
								}

								clientOut.writePacket(new FinishConfigPacket());
							}
							if (p.id == AckFinishConfigPacket.packetId) {
								System.out.println("Finished configuration.");
								connectionState = ConnectionState.PLAY;

								initUser();
							}
							break;
						case DISCONNECTED:
							break;
						case PLAY:
							if (p.id == TeleportConfirmPacket.packetId) {
								TeleportConfirmPacket conf = p.convert(new TeleportConfirmPacket());
								awaitingTps.remove(conf.value);
								continue;

							}
							if (p.id == PlayerMovePacket.packetId ||//
									p.id == PlayerMovePositionPacket.packetId ||//
									p.id == PlayerMovePositionRotationPacket.packetId ||//
									p.id == PlayerMoveRotationPacket.packetId) {
								if (!awaitingTps.isEmpty()) {
									continue;
								}
							}

							if (p.id == AckChunksPacket.packetId) {
								continue;
							}

							if (p.id == ExecuteCommandPacket.packetId || p.id == ExecuteCommandSignedPacket.packetId) {
								ExecuteCommandPacket command = p.convert(new ExecuteCommandPacket());
								if (runCommand(command.command)) {
									continue;
								}
							}

							if (linked == null) {
								continue;
							}

							updatePositionFor(p, clientPos);

							if (viewMode != 0) {
								if (p.id == PlayerMovePositionPacket.packetId//
										|| p.id == PlayerMovePositionRotationPacket.packetId) {
									PositionData pos = linked.getData(PositionData.class);
									if ((viewMode & (FORCE_MOVE | FORCE_LOOK)) != 0) {

										PlayerMovePositionPacket move = p.convert(new PlayerMovePositionPacket());

										if (move.x != pos.pos.x || move.y != pos.pos.y || move.z != pos.pos.z) {
											TeleportRequestPacket tp = new TeleportRequestPacket();

											tp.x = pos.pos.x;
											tp.y = pos.pos.y;
											tp.z = pos.pos.z;
											if ((viewMode & FORCE_LOOK) != 0) {
												tp.pitch = pos.pitch;
												tp.yaw = pos.yaw;
											} else {
												tp.pitch = 0;
												tp.yaw = 0;
												tp.flags |= 0x08 | 0x10;
											}
											tp.teleportID = Integer.MAX_VALUE - 1;

											awaitingTps.add(tp.teleportID);
											clientOut.writePacket(tp);
										}
										continue;
									}
								}

								InventoryData inv = linked.getData(InventoryData.class);
								if (p.id == ClickContainerPacket.packetId) {
									if ((viewMode & FORCE_INVENTORY) != 0) {
										clientOut.writePacket(new SetContainerContentPacket(inv.inv));
										if (inv.screen != null) {
											clientOut.writePacket(new SetContainerContentPacket(inv.screen));
										}
										continue;
									}
								}

								if (p.id == HotbarSlotPacket.packetId) {
									if ((viewMode & FORCE_INVENTORY) != 0) {
										clientOut.writePacket(new SetHeldItemPacket((byte) inv.hotbarSlot));
										continue;
									}
								}

								if (p.id == UseItemOnPacket.packetId) {
									if ((viewMode & FORCE_BLOCKS) != 0) {
										UseItemOnPacket use = p.convert(new UseItemOnPacket());
										WorldData world = linked.getData(WorldData.class);

										for (int i = -1; i < 2; i++) {
											for (int j = -1; j < 2; j++) {
												for (int k = -1; k < 2; k++) {
													Position update = use.pos.copy().add(i, j, k);
													int block = world.world.getBlock(update);
													if (block >= 0) {
														clientOut.writePacket(new BlockUpdatePacket(update, block));
													}
												}
											}
										}

										clientOut.writePacket(new AckBlockChangePacket(use.sequence));

										if ((viewMode & FORCE_INVENTORY) != 0) {
											clientOut.writePacket(new SetContainerContentPacket(inv.inv));
											if (inv.screen != null) {
												clientOut.writePacket(new SetContainerContentPacket(inv.screen));
											}
										}
										continue;
									}
								}

								if (p.id == PlayerActionPacket.packetId) {
									WorldData world = linked.getData(WorldData.class);

									PlayerActionPacket action = p.convert(new PlayerActionPacket());
									if (action.status == PlayerActionPacket.FINISH_MINING //
											|| action.status == PlayerActionPacket.START_MINING//
											|| action.status == PlayerActionPacket.CANCEL_MINING) {
										if ((viewMode & FORCE_BLOCKS) != 0) {
											int block = world.world.getBlock(action.pos);
											if (block >= 0) {
												clientOut.writePacket(new AckBlockChangePacket(action.sequence));
												clientOut.writePacket(new BlockUpdatePacket(action.pos, block));
											}
											continue;
										}
									}

									if (action.status == PlayerActionPacket.SWAP_HANDS//
											|| action.status == PlayerActionPacket.DROP_ITEM//
											|| action.status == PlayerActionPacket.DROP_STACK) {
										if ((viewMode & FORCE_INVENTORY) != 0) {
											clientOut.writePacket(new SetContainerContentPacket(inv.inv));
											if (inv.screen != null) {
												clientOut.writePacket(new SetContainerContentPacket(inv.screen));
											}
											continue;
										}
									}

								}

								if ((viewMode & FORCE_BLOCK_OUTGOING) != 0) continue;
							}

							PositionData pos = linked.getData(PositionData.class);
							updatePositionFor(p, pos);

							if (p.id == KeepAliveReplyPacket.packetId) {
								continue;
							}

							synchronized (linked) {
								if (p.id == ClickContainerPacket.packetId) {
									InventoryData inv = linked.getData(InventoryData.class);
									ClickContainerPacket click = p.convert(new ClickContainerPacket());

									for (var change : click.changed) {
										inv.getActiveScreen().slots[change.index].become(change.slot);
									}

									inv.getActiveScreen().cursor.become(click.carriedItem);

								} else if (p.id == HotbarSlotPacket.packetId) {
									HotbarSlotPacket slot = p.convert(new HotbarSlotPacket());
									InventoryData inv = linked.getData(InventoryData.class);
									inv.hotbarSlot = slot.slot;
								} else if (p.id == CloseContainerPacket.packetId) {
									InventoryData inv = linked.getData(InventoryData.class);
									inv.closeScreen();
									continue;
								}

								linked.sendPacket(p);
								if (!secondaryLinks.isEmpty()) {
									secondaryLinks.forEach(sc2 -> {
										synchronized (sc2) {
											sc2.sendPacket(p);
										}
									});
								}
							}
							break;
						default:
							break;

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (linked != null) linked.removePlayHandler(this);
				try {
					client.close();
				} catch (IOException e1) {
				}
			}

		}).start();

		long start = System.currentTimeMillis();
		new Thread(() -> {
			try {
				while (true) {
					if (connectionState == ConnectionState.PLAY) {
						clientOut.writePacket(new KeepAlivePacket(System.currentTimeMillis()));
					} else {
						if (System.currentTimeMillis() - start > 30000) {
							break;
						}
					}
					Thread.sleep(10000);

				}
			} catch (Exception e) {
				//				e.printStackTrace();
			}

		}).start();
	}

	boolean spawned = false;
	boolean finishedInit = false;
	int previousPlayerId = 0;
	ServerConnection previousLink = null;

	private void initUser() throws IOException {
		finishedInit = false;
		PositionData pos = linked.getData(PositionData.class);
		WorldData world = linked.getData(WorldData.class);
		PlayerData player = linked.getData(PlayerData.class);
		InventoryData inv = linked.getData(InventoryData.class);
		ViewerTrackPlayData tracked = linked.getData(ViewerTrackPlayData.class);

		if (spawned) {
			//			clientOut.writePacket(new DeathPacket(previousPlayerId, new NBTTagString("Respawn")));
			//			clientOut.writePacket(new SetHealthPacket(0, 1, 1));
			//			synchronized (world.world.worlds) {
			//				for (Chunk c : world.world.chunks.values()) {
			//					if (c.players.contains(previousLink)) {
			//						clientOut.writePacket(new UnloadChunkPacket(c.chunkX, c.chunkZ));
			//					}
			//				}
			//			}
			ViewerTrackPlayData trackedOld = previousLink.getData(ViewerTrackPlayData.class);
			UUID[] remove;
			synchronized (trackedOld.playerList) {
				remove = new UUID[trackedOld.playerList.size()];
				int i = 0;
				for (var key : trackedOld.playerList.keySet()) {
					remove[i] = key;
					i++;
				}
			}
			clientOut.writePacket(new PlayerInfoRemovePacket(remove));
		}
		previousLink = linked;
		previousPlayerId = player.entityId;
		if (tracked.loginPacket == null) {
			writePacket(new DisconnectPacket(new NBTTagString("Bot not yet logged in.")));
		}
		clientOut.writePacket(tracked.loginPacket);

		if (spawned) {
			RespawnPacket respawn = new RespawnPacket();
			respawn.dataKept = 0;
			respawn.deathDimension = tracked.loginPacket.deathDimension;
			respawn.deathLocation = tracked.loginPacket.deathLocation;
			respawn.dimensionName = player.pos.dimension;
			respawn.dimensionType = tracked.loginPacket.dimensionType;
			respawn.gamemode = tracked.loginPacket.gamemode;
			respawn.hasDeathLocation = tracked.loginPacket.hasDeathLocation;
			respawn.isDebug = tracked.loginPacket.isDebug;
			respawn.isFlat = tracked.loginPacket.isFlat;
			respawn.portalCooldown = tracked.loginPacket.portalCooldown;
			respawn.seedHash = tracked.loginPacket.seedHash;
			respawn.previousGamemode = tracked.loginPacket.previousGamemode;
			clientOut.writePacket(respawn);

		}

		clientPos.dimension = player.pos.dimension;

		if (tracked.commands == null) {
			writePacket(new DisconnectPacket(new NBTTagString("Bot not yet logged in.")));
			kick();
			return;
		}
		clientOut.writePacket(tracked.commands);

		synchronized (tracked.playerList) {
			clientOut.writePacket(new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.ALL, tracked.playerList));
		}

		spawned = true;

		TeleportRequestPacket tp = new TeleportRequestPacket();
		tp.x = pos.pos.x;
		tp.y = pos.pos.y;
		tp.z = pos.pos.z;
		tp.pitch = pos.pitch;
		tp.yaw = pos.yaw;
		tp.teleportID = Integer.MAX_VALUE;

		awaitingTps.add(tp.teleportID);
		clientOut.writePacket(tp);

		int orbId = Registries.registries.get("minecraft:entity_type").get("minecraft:experience_orb");
		synchronized (world.world.worlds) {
			for (Entity e : world.world.entities.values()) {
				if (e.players.contains(linked)) {
					if (e.type == orbId) {
						XPOrbSpawnPacket orb = new XPOrbSpawnPacket();
						orb.count = (short) e.data;
						orb.entityID = e.entityId;
						orb.x = e.pos.x;
						orb.y = e.pos.y;
						orb.z = e.pos.z;
						clientOut.writePacket(orb);
					} else {
						clientOut.writePacket(new EntitySpawnPacket(e));
						boolean equipment = false;
						for (Slot s : e.slots) {
							if (s != null) {
								equipment = true;
								break;
							}
						}
						if (equipment) {
							clientOut.writePacket(new EntityEquipmentPacket(e));
						}

						boolean metadata = false;
						for (EntityMetadata s : e.metadata) {
							if (s != null) {
								metadata = true;
								break;
							}
						}
						if (metadata) {
							clientOut.writePacket(new EntityMetaDataPacket(e));
						}
					}
				}
			}
		}
		clientOut.writePacket(new SetHealthPacket(player.health, player.food, player.saturation));
		clientOut.writePacket(new SetContainerContentPacket(inv.inv));
		if (inv.screen != null) {
			clientOut.writePacket(new OpenContainerPacket(inv.screen));
			clientOut.writePacket(new SetContainerContentPacket(inv.screen));
		}
		clientOut.writePacket(new SetHeldItemPacket((byte) inv.hotbarSlot));

		clientOut.writePacket(new GameEventPacket(GameEventPacket.START_WAITING_FOR_CHUNKS));

		Position playerBlock = new Position(pos.pos);

		clientOut.writePacket(new SetChunkCenterPacket(playerBlock.x >> 4, playerBlock.z >> 4));
		clientOut.writePacket(new ChunkBatchStartPacket());

		int sent = 0;
		synchronized (world.world.worlds) {
			for (Chunk c : world.world.chunks.values()) {
				if (c.players.contains(linked)) {
					ChunkDataPacket loadChunk = new ChunkDataPacket();
					loadChunk.chunkX = c.chunkX;
					loadChunk.chunkZ = c.chunkZ;
					loadChunk.useBlockEntities(c.blockEntities.values());
					loadChunk.heightmap = new NBTTagCompound();
					loadChunk.data = ChunkDataEncoder.encode(c);
					clientOut.writePacket(loadChunk);
					sent++;
				}
			}
		}

		clientOut.writePacket(new ChunkBatchFinishPacket(sent));

		tp = new TeleportRequestPacket();
		tp.x = pos.pos.x;
		tp.y = pos.pos.y;
		tp.z = pos.pos.z;
		tp.pitch = pos.pitch;
		tp.yaw = pos.yaw;
		tp.teleportID = Integer.MAX_VALUE - 1;

		awaitingTps.add(tp.teleportID);
		clientOut.writePacket(tp);
		System.out.println("Wrote tp");

		finishedInit = true;
	}

	public void writePacket(Packet p) {
		try {
			clientOut.writePacket(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean runCommand(String command) {
		for (var p : commandHandlers) {
			try {
				if (p.test(this, command)) {
					return true;
				}
			} catch (Exception e) {
				SystemChatPacket reply = new SystemChatPacket();
				reply.content = new NBTTagString("§cInternal error running command.");
				writePacket(reply);
				System.err.println("Error running proxy command: " + command);
				e.printStackTrace();
			}
		}
		return false;
	}

	public void moveLink(ServerConnection sc) {
		if (linked != null) {
			synchronized (linked) {
				linked.removePlayHandler(this);
				linked = sc;
			}
		} else {
			linked = sc;
		}
		synchronized (linked) {
			linked.addPlayHandler(this);
		}
		try {
			initUser();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void kick() {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void kick(String message) {
		try {
			NBTTagString nbt = new NBTTagString(message);
			if (connectionState == ConnectionState.PLAY) {
				clientOut.writePacket(new DisconnectPacket(nbt));
			}
			if (connectionState == ConnectionState.CONFIGURATION) {
				clientOut.writePacket(new DisconnectConfigPacket(nbt));
			}
			if (connectionState == ConnectionState.LOGIN) {
				clientOut.writePacket(new DisconnectLoginPacket(nbt));
			}
			clientOut.flush();
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
			kick();
		}
	}

	public void handlePacket(UnknownPacket packet, ServerConnection sc) throws IOException {
		if (sc != linked) {
			System.out.println("Handling packets from incorrect ServerConnection " + sc.name);
			sc.removePlayHandler(this);
			return;
		}

		if (connectionState != ConnectionState.PLAY) {
			return;
		}

		if (!finishedInit) {
			return;
		}

		//		String packetName = PacketIds.getS2CPacketName(connectionState.name, packet.id);
		//		if (!List.of("minecraft:level_chunk_with_light", "minecraft:move_entity_pos", "minecraft:set_entity_motion", "minecraft:rotate_head", "minecraft:move_entity_pos_rot", "minecraft:teleport_entity", "minecraft:set_entity_data", "minecraft:set_time", "minecraft:entity_event", "minecraft:bundle_delimiter", "minecraft:add_entity", "minecraft:update_attributes").contains(packetName)) {
		//			System.out.println("Server packet: " + packetName);
		//		}
		//		if (packetName.equals("minecraft:game_event")) {
		//			GameEventPacket event = packet.convert(new GameEventPacket());
		//			System.out.println("Game event: " + event.type + " " + event.value);
		//		}

		try {
			clientOut.writePacket(packet);
		} catch (Exception e) {
			e.printStackTrace();
			sc.removePlayHandler(this);
			try {
				client.close();
			} catch (Exception e2) {

			}
		}

	}

	Vector lastPos = new Vector();

	public void tick(ServerConnection sc) throws IOException {
		//		clientOut.flush();
		if (sc != linked) {
			System.out.println("Handling packets from incorrect ServerConnection " + sc.name);
			sc.removePlayHandler(this);
			return;
		}

		if (connectionState != ConnectionState.PLAY) {
			return;
		}

		if (!finishedInit) {
			return;
		}
		PositionData pos = linked.getData(PositionData.class);

		if (pos.pos.distanceTo(lastPos) > 1 && (viewMode & FORCE_MOVE) != 0) {
			TeleportRequestPacket tp = new TeleportRequestPacket();
			tp.x = pos.pos.x;
			tp.y = pos.pos.y;
			tp.z = pos.pos.z;
			tp.pitch = 0;
			tp.yaw = 0;
			tp.flags |= 0x08 | 0x10;
			tp.teleportID = Integer.MAX_VALUE - 1;

			awaitingTps.add(tp.teleportID);
			clientOut.writePacket(tp);
		}

		lastPos = pos.pos.copy();
	}

	public boolean handlesTick() {
		return true;
	}

	public List<Integer> handlesPackets() {
		return handlesPackets;
	}
}
