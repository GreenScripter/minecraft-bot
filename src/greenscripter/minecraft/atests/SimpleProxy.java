package greenscripter.minecraft.atests;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import greenscripter.minecraft.ServerConnection.ConnectionState;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.nbt.NBTTagCompound;
import greenscripter.minecraft.nbt.NBTTagList;
import greenscripter.minecraft.nbt.NBTTagString;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.configuration.AckFinishConfigPacket;
import greenscripter.minecraft.packet.c2s.handshake.HandshakePacket;
import greenscripter.minecraft.packet.c2s.login.LoginAcknowledgePacket;
import greenscripter.minecraft.packet.c2s.play.KeepAliveReplyPacket;
import greenscripter.minecraft.packet.s2c.login.SetCompressionPacket;
import greenscripter.minecraft.packet.s2c.play.DisconnectPacket;
import greenscripter.minecraft.packet.s2c.play.KeepAlivePacket;
import greenscripter.minecraft.packet.s2c.play.SystemChatPacket;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SimpleProxy {

	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		ServerSocket ss = new ServerSocket(25568);

		String ip = "localhost";
		int port = 20255;

		while (true) {
			Socket client = ss.accept();
			new Thread(() -> {
				try {
					Socket server = new Socket(ip, port);
					new SimpleProxyConnection(client, server);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}).start();

		}
	}

	static class SimpleProxyConnection {

		Socket client;
		MCInputStream clientIn;
		MCOutputStream clientOut;

		Socket server;
		MCInputStream serverIn;
		MCOutputStream serverOut;

		ConnectionState connectionState = ConnectionState.HANDSHAKE;
		long lastKeepAlive = System.currentTimeMillis();

		public SimpleProxyConnection(Socket client, Socket server) throws IOException {
			this.client = client;
			this.server = server;

			clientIn = new MCInputStream(client.getInputStream());
			serverOut = new MCOutputStream(server.getOutputStream());

			serverIn = new MCInputStream(server.getInputStream());
			clientOut = new MCOutputStream(client.getOutputStream());

			new Thread(() -> {
				try {
					while (true) {
						UnknownPacket p = serverIn.readGeneralPacket();

						// Track keep alive packets.
						if (connectionState == ConnectionState.PLAY && p.id == KeepAlivePacket.packetId) {
							lastKeepAlive = System.currentTimeMillis();
						}

						// Ignore disconnect packets.
						if (connectionState == ConnectionState.PLAY && p.id == DisconnectPacket.packetId) {
							NBTComponent kickMessage = p.convert(new DisconnectPacket()).reason;
							
							NBTTagCompound chatMessage = new NBTTagCompound();
							chatMessage.put("text", new NBTTagString("You were kicked from the server: "));
							chatMessage.put("color", new NBTTagString("#FF0000"));
							
							NBTTagList<NBTTagCompound> extra = new NBTTagList<>(NBTComponent.TAG_Compound);
							chatMessage.components.put("extra", extra);
							if (kickMessage.isString()) {
								extra.value.add(new NBTTagCompound("text", kickMessage));
							} else {
								extra.value.add(kickMessage.asCompound());
							}
							
							SystemChatPacket messagePacket = new SystemChatPacket(chatMessage);
							clientOut.writePacket(messagePacket);
							continue;
						}
						// Send keep alives to server.
						if (client.isClosed()) {
							if (connectionState == ConnectionState.PLAY && p.id == KeepAlivePacket.packetId) {
								serverOut.writePacket(new KeepAliveReplyPacket(p.convert(new KeepAlivePacket()).value));
								continue;
							}
						}

						// Enable compression.
						if (connectionState == ConnectionState.LOGIN && p.id == SetCompressionPacket.packetId) {
							SetCompressionPacket compression = p.convert(new SetCompressionPacket());
							clientOut.writePacket(p);
							if (compression.value >= 0) {
								clientIn.compression = true;
								serverIn.compression = true;
								clientOut.compressionThreshold = compression.value;
								serverOut.compressionThreshold = compression.value;
							}
							continue;
						}

						// Client is disconnected so don't forward.
						if (client.isClosed()) {
							continue;
						}

						// Forward packet.
						try {
							clientOut.writePacket(p);
						} catch (IOException e) {
							e.printStackTrace();
							client.close();
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}).start();

			new Thread(() -> {
				try {
					while (true) {
						UnknownPacket p = clientIn.readGeneralPacket();
						//						System.out.println(connectionState + " " + p.id);
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
										connectionState = ConnectionState.QUERY;
									}
								}
								break;
							case QUERY:
								break;
							case LOGIN:
								if (p.id == LoginAcknowledgePacket.packetId) {
									connectionState = ConnectionState.CONFIGURATION;
								}
								break;
							case CONFIGURATION:
								if (p.id == AckFinishConfigPacket.packetId) {
									System.out.println("Finished configuration.");
									connectionState = ConnectionState.PLAY;
								}
								break;
							case DISCONNECTED:
								break;
							case PLAY:
								if (System.currentTimeMillis() - lastKeepAlive > 20000) {
									clientOut.writePacket(new KeepAlivePacket(lastKeepAlive));
								}
								break;
							default:
								break;

						}

						if (server.isClosed()) {
							// Server is disconnected so don't forward.
							continue;
						}

						try {
							serverOut.writePacket(p);
						} catch (IOException e) {
							e.printStackTrace();
							server.close();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}).start();
		}
	}
}
