package greenscripter.minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.configuration.AckFinishConfigPacket;
import greenscripter.minecraft.packet.c2s.configuration.ClientInfoConfigPacket;
import greenscripter.minecraft.packet.c2s.configuration.KeepAliveReplyConfigPacket;
import greenscripter.minecraft.packet.c2s.handshake.HandshakePacket;
import greenscripter.minecraft.packet.c2s.login.LoginAcknowledgePacket;
import greenscripter.minecraft.packet.c2s.login.LoginStartPacket;
import greenscripter.minecraft.packet.s2c.configuration.KeepAliveConfigPacket;
import greenscripter.minecraft.packet.s2c.configuration.RegistryConfigPacket;
import greenscripter.minecraft.packet.s2c.login.LoginSuccessPacket;
import greenscripter.minecraft.packet.s2c.login.SetCompressionPacket;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.play.state.PlayState;
import greenscripter.minecraft.play.state.RegistryState;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.PeekInputStream;

public class ServerConnection {

	public Socket socket;
	public MCInputStream in;
	public MCOutputStream out;

	public String name;
	public UUID uuid;
	public String hostname;
	public int port;
	public int id;

	public boolean bungeeMode = false;

	public State state = State.LOGIN;

	public boolean blocking = false;

	private List<PlayHandler> handlers = new ArrayList<>();

	private Map<Class<? extends PlayState>, PlayState> playState = new HashMap<>();

	@SuppressWarnings("unchecked")
	private List<PlayHandler>[] packetTypes = new List[200];
	private List<PlayHandler> ticking = new ArrayList<>();

	SocketChannel channel;

	PeekInputStream peeker;

	public ServerConnection(String hostname, int port, String name, UUID uuid, List<PlayHandler> playHandler) throws IOException {
		channel = SocketChannel.open();
		channel.connect(new InetSocketAddress(hostname, port));
		channel.configureBlocking(true);
		this.socket = channel.socket();//new Socket(hostname, port);
		socket.setReceiveBufferSize(1024 * 1024 * 1);
		peeker = new PeekInputStream(socket.getInputStream(), channel);
		in = new MCInputStream(peeker);
		out = new MCOutputStream(socket.getOutputStream());
		state = State.HANDSHAKE;
		this.name = name;
		this.uuid = uuid;
		this.hostname = hostname;
		this.port = port;
		playHandler.forEach(this::addPlayHandler);
	}

	public ServerConnection addPlayHandler(PlayHandler p) {
		handlers.add(p);
		for (int i : p.handlesPackets()) {
			if (packetTypes[i] == null) packetTypes[i] = new ArrayList<>();
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

	public ServerConnection removePlayHandler(PlayHandler p) {
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
	public <T extends PlayState> T getState(Class<T> type) {
		PlayState s = playState.get(type);
		if (s == null) {
			s = PlayState.createState(type);
			playState.put((Class<? extends PlayState>) type, s);
		}
		return (T) s;
	}

	public void step() throws IOException {
		switch (state) {
			case HANDSHAKE -> {
				String hostname = this.hostname;
				if (bungeeMode) {
					hostname += "\u0000" + "127.0.0.1" + "\u0000" + uuid;
				}
				out.writePacket(new HandshakePacket(hostname, port, 2));
				state = State.LOGIN;
			}
			case LOGIN -> {
				out.writePacket(new LoginStartPacket(name, uuid));

				UnknownPacket p = in.readGeneralPacket();
				int compression = -1;
				if (p.id() == 3) {
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
				LoginSuccessPacket success = p.convert(new LoginSuccessPacket());
				System.out.println("Logged in " + success.name + " " + success.uuid + " " + success.properties);
				out.writePacket(new LoginAcknowledgePacket());
				state = State.CONFIGURATION;
				//				System.out.println("Finished Login");
			}
			case CONFIGURATION -> {
				out.writePacket(new ClientInfoConfigPacket());
				UnknownPacket p = in.readGeneralPacket();
				if (p.id == 2) {
					out.writePacket(new AckFinishConfigPacket());
					state = State.PLAY;
					//					System.out.println("Finished Configuration");
				}
				if (p.id == 3) {
					out.writePacket(new KeepAliveReplyConfigPacket(p.convert(new KeepAliveConfigPacket()).value));
				}
				if (p.id == 5) {
					RegistryConfigPacket rp = p.convert(new RegistryConfigPacket());
					getState(RegistryState.class).configuredRegistry = rp.data;
				}
			}

			case PLAY -> {
				//				in.available();
				if (!blocking && !(peeker.peek() > 0)) return;
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
			default -> throw new IllegalArgumentException("Unexpected value: " + state);
		}
	}

	public void tick() throws IOException {
		for (PlayHandler h : ticking) {
			h.tick(this);
		}
	}

	public boolean waiting() throws IOException {
		return !(in.available() > 0);
	}

	public static enum State {
		HANDSHAKE, LOGIN, CONFIGURATION, PLAY, DISCONNECTED;
	}

	public String toString() {
		return name;
	}
}
