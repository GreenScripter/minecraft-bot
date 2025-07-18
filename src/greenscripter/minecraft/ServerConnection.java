package greenscripter.minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.configuration.AckFinishConfigPacket;
import greenscripter.minecraft.packet.c2s.configuration.ClientInfoConfigPacket;
import greenscripter.minecraft.packet.c2s.configuration.ClientKnownPacksConfigPacket;
import greenscripter.minecraft.packet.c2s.configuration.KeepAliveReplyConfigPacket;
import greenscripter.minecraft.packet.c2s.configuration.PingReplyConfigPacket;
import greenscripter.minecraft.packet.c2s.handshake.HandshakePacket;
import greenscripter.minecraft.packet.c2s.login.LoginAcknowledgePacket;
import greenscripter.minecraft.packet.c2s.login.LoginStartPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionRotationPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMoveRotationPacket;
import greenscripter.minecraft.packet.s2c.configuration.DisconnectConfigPacket;
import greenscripter.minecraft.packet.s2c.configuration.KeepAliveConfigPacket;
import greenscripter.minecraft.packet.s2c.configuration.PingConfigPacket;
import greenscripter.minecraft.packet.s2c.configuration.RegistryConfigPacket;
import greenscripter.minecraft.packet.s2c.configuration.ServerKnownPacksConfigPacket;
import greenscripter.minecraft.packet.s2c.login.DisconnectLoginPacket;
import greenscripter.minecraft.packet.s2c.login.LoginSuccessPacket;
import greenscripter.minecraft.packet.s2c.login.SetCompressionPacket;
import greenscripter.minecraft.play.data.PlayData;
import greenscripter.minecraft.play.data.RegistryData;
import greenscripter.minecraft.play.handler.DeathPlayHandler;
import greenscripter.minecraft.play.handler.DisconnectHandler;
import greenscripter.minecraft.play.handler.EntityPlayHandler;
import greenscripter.minecraft.play.handler.InventoryPlayHandler;
import greenscripter.minecraft.play.handler.KeepAlivePlayHandler;
import greenscripter.minecraft.play.handler.PingPongPlayHandler;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.play.handler.PlayerPlayHandler;
import greenscripter.minecraft.play.handler.StatisticsHandler;
import greenscripter.minecraft.play.handler.TeleportRequestPlayHandler;
import greenscripter.minecraft.play.handler.WorldPlayHandler;
import greenscripter.minecraft.utils.BlockingNonBlockingOutputStream;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.PeekInputStream;

public class ServerConnection {

	public Thread owner = null;

	public Socket socket;
	public MCInputStream in;
	public MCOutputStream out;

	public String name;
	public UUID uuid;
	public String targetName;
	public UUID targetUuid;
	public String hostname;
	public int port;
	public int id;

	public boolean bungeeMode = false;

	public ConnectionState connectionState = ConnectionState.LOGIN;

	public boolean blocking = false;

	private List<PlayHandler> handlers = new CopyOnWriteArrayList<>();

	private Map<Class<? extends PlayData>, PlayData> playData = new HashMap<>();

	@SuppressWarnings("unchecked")
	private List<PlayHandler>[] packetTypes = new List[200];
	private List<PlayHandler> ticking = new CopyOnWriteArrayList<>();

	SocketChannel channel;

	PeekInputStream peeker;

	public ServerConnection(String hostname, int port, String name, UUID uuid, List<PlayHandler> playHandler) {
		this.name = name;
		this.uuid = uuid;
		this.targetName = name;
		this.targetUuid = uuid;
		this.hostname = hostname;
		this.port = port;
		if (playHandler != null) playHandler.forEach(this::addPlayHandler);
	}

	public synchronized void connect() throws IOException {
		name = targetName;
		uuid = targetUuid;
		channel = SocketChannel.open();
		channel.connect(new InetSocketAddress(hostname, port));
		channel.configureBlocking(false);
		this.socket = channel.socket();//new Socket(hostname, port);
		socket.setSoTimeout(20000);
		socket.setReceiveBufferSize(1024 * 1024 * 1);
		peeker = new PeekInputStream(channel);
		in = new MCInputStream(peeker);
		out = new MCOutputStream(new BufferedOutputStream(new BlockingNonBlockingOutputStream(channel)));
		connectionState = ConnectionState.HANDSHAKE;
	}

	public synchronized ServerConnection addPlayHandler(PlayHandler p) {
		handlers.add(p);
		for (int i : p.handlesPackets()) {
			if (packetTypes[i] == null) packetTypes[i] = new CopyOnWriteArrayList<>();
			packetTypes[i].add(p);
		}
		if (p.handlesTick()) {
			ticking.add(p);
		}
		return this;
	}

	public List<PlayHandler> getPlayHandlers() {
		return new ArrayList<>(handlers);
	}

	public synchronized ServerConnection removePlayHandler(PlayHandler p) {
		handlers.remove(p);
		ticking.remove(p);
		for (List<PlayHandler> l : packetTypes) {
			if (l != null) {
				l.remove(p);
			}
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T extends PlayData> T getData(Class<T> type) {
		PlayData s = playData.get(type);
		if (s == null) {
			s = PlayData.createData(type, this);
			playData.put((Class<? extends PlayData>) type, s);
		}
		return (T) s;
	}

	public synchronized <T extends PlayData> void setData(Class<T> type, T t) {
		playData.put(type, t);
	}

	public synchronized void step() throws IOException {
		switch (connectionState) {
			case HANDSHAKE -> {
				String hostname = this.hostname;
				if (bungeeMode) {
					hostname += "\u0000" + "127.0.0.1" + "\u0000" + uuid;
				}
				out.writePacket(new HandshakePacket(hostname, port, 2));
				connectionState = ConnectionState.LOGIN;
			}
			case LOGIN -> {
				out.writePacket(new LoginStartPacket(name, uuid));

				UnknownPacket p = in.readGeneralPacket();
				int compression = -1;
				if (p.id() == SetCompressionPacket.packetId) {
					var compress = p.convert(new SetCompressionPacket());
					if (compress.value >= 0) {
						compression = compress.value;
						//						System.out.println("Set compression to " + compression);
						in.compression = true;
						out.compressionThreshold = compression;
					}
					//			System.out.println(Arrays.toString(in.readAllBytes()));
					p = in.readGeneralPacket();
				}
				if (p.id == DisconnectLoginPacket.packetId) {
					System.out.println(p.convert(new DisconnectLoginPacket()).reason);
					socket.close();
				} else {
					LoginSuccessPacket success = p.convert(new LoginSuccessPacket());
					System.out.println("Logged in " + success.name + " " + success.uuid + " " + success.properties);
					name = success.name;
					uuid = success.uuid;
					out.writePacket(new LoginAcknowledgePacket());
					connectionState = ConnectionState.CONFIGURATION;
				}
				//				System.out.println("Finished Login");
			}
			case CONFIGURATION -> {
				out.writePacket(new ClientInfoConfigPacket());
				UnknownPacket p = in.readGeneralPacket();

				if (p.id == PingConfigPacket.packetId) {
					out.writePacket(new PingReplyConfigPacket(p.convert(new PingConfigPacket()).value));
				}
				if (p.id == DisconnectConfigPacket.packetId) {
					System.out.println(p.convert(new DisconnectConfigPacket()).reason);
					socket.close();
				}
				if (p.id == AckFinishConfigPacket.packetId) {
					out.writePacket(new AckFinishConfigPacket());
					connectionState = ConnectionState.PLAY;
					//					System.out.println("Finished Configuration");
				}
				if (p.id == KeepAliveReplyConfigPacket.packetId) {
					out.writePacket(new KeepAliveReplyConfigPacket(p.convert(new KeepAliveConfigPacket()).value));
				}
				if (p.id == RegistryConfigPacket.packetId) {
					RegistryConfigPacket rp = p.convert(new RegistryConfigPacket());
					getData(RegistryData.class).registries.put(rp.registry.name, rp.registry);
				}
				if (p.id == ServerKnownPacksConfigPacket.packetId) {
					out.writePacket(new ClientKnownPacksConfigPacket());
				}
			}

			case PLAY -> {
				//				in.available();
				if (!blocking && !peeker.peekPacket()) return;
				//				if (!blocking && !(in.available() > 1)) return;

				UnknownPacket p = in.readGeneralPacket();

				//				byte[] packetData = in.readPacket();
				//				int id = MCInputStream.readPacketID(packetData);
				List<PlayHandler> handlers = packetTypes[p.id];
				if (handlers != null) {
					//					UnknownPacket p = MCInputStream.readGeneralPacketOf(packetData);

					for (PlayHandler h : handlers) {
						h.handlePacket(p, this);
					}
				}
			}
			default -> throw new IllegalArgumentException("Unexpected value: " + connectionState);
		}
	}

	public void sendPacket(Packet p) {
		synchronized (this) {
			try {
				if (connectionState == ConnectionState.PLAY && owner == Thread.currentThread()) {
					this.out.writePacketNoFlush(p);
				} else {
					this.out.writePacket(p);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void tick() throws IOException {
		for (PlayHandler h : ticking) {
			h.tick(this);
		}
	}

	public boolean waiting() throws IOException {
		return !peeker.peekPacket();
	}

	public static enum ConnectionState {

		HANDSHAKE("handshake"), STATUS("status"), LOGIN("login"), CONFIGURATION("configuration"), PLAY("play"), DISCONNECTED(null);

		public final String name;

		ConnectionState(String name) {
			this.name = name;
		}
	}

	public String toString() {
		return name;
	}

	public static List<PlayHandler> getStandardHandlers() {
		return new ArrayList<>(List.of(//
				new KeepAlivePlayHandler(), //
				new PingPongPlayHandler(), //
				new DeathPlayHandler(), //
				new WorldPlayHandler(), //
				new TeleportRequestPlayHandler(),//
				new EntityPlayHandler(),//
				new PlayerPlayHandler(),//
				new InventoryPlayHandler(),//
				new DisconnectHandler(),//
				new StatisticsHandler()//
		));
	}
}
