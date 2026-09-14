package com.greenscripter.minecraftbot.atests;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.greenscripter.minecraftbot.ServerConnection.ConnectionState;
import com.greenscripter.minecraftbot.nbt.NBTComponent;
import com.greenscripter.minecraftbot.nbt.NBTTagCompound;
import com.greenscripter.minecraftbot.nbt.NBTTagList;
import com.greenscripter.minecraftbot.nbt.NBTTagString;
import com.greenscripter.minecraftbot.packet.UnknownPacket;
import com.greenscripter.minecraftbot.packet.c2s.configuration.AckFinishConfigPacket;
import com.greenscripter.minecraftbot.packet.c2s.handshake.HandshakePacket;
import com.greenscripter.minecraftbot.packet.c2s.login.LoginAcknowledgePacket;
import com.greenscripter.minecraftbot.packet.c2s.play.KeepAliveReplyPacket;
import com.greenscripter.minecraftbot.packet.s2c.login.SetCompressionPacket;
import com.greenscripter.minecraftbot.packet.s2c.play.DisconnectPacket;
import com.greenscripter.minecraftbot.packet.s2c.play.KeepAlivePacket;
import com.greenscripter.minecraftbot.packet.s2c.play.SystemChatPacket;
import com.greenscripter.minecraftbot.packet.s2c.status.StatusResponsePacket;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

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
						//						String name = PacketIds.getS2CPacketName(connectionState.name, p.id);

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
							chatMessage.put("extra", extra);
							if (kickMessage.isString()) {
								extra.add(new NBTTagCompound("text", kickMessage));
							} else {
								extra.add(kickMessage.asCompound());
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
						//						if (connectionState == ConnectionState.PLAY && p.id == PacketIds.getS2CPlayId("minecraft:light_update")) {
						//							continue;
						//						}
						//						if (connectionState == ConnectionState.PLAY && p.id == ChunkDataPacket.packetId) {
						//							ChunkDataPacket chunk = p.convert(new ChunkDataPacket());
						//							chunk.heightmap = new NBTTagCompound();
						//							Chunk c = new Chunk(chunk.chunkX, chunk.chunkZ, -64, 384, null);
						//
						//							ChunkDataDecoder.decode(c, chunk.data);
						//							chunk.data = ChunkDataEncoder.encode(c);
						//
						//							clientOut.writePacket(chunk);
						//							continue;
						//						}

						// Enable compression.
						if (connectionState == ConnectionState.LOGIN && p.id == SetCompressionPacket.packetId) {
							SetCompressionPacket compression = p.convert(new SetCompressionPacket());
							clientOut.writePacket(p);
							if (compression.value >= 0) {
								clientIn.compression = true;
								serverIn.compression = true;
								clientOut.compressionThreshold = compression.value;
								serverOut.compressionThreshold = compression.value;
								clientOut.actuallyCompress = true;
								serverOut.actuallyCompress = true;
							}
							continue;
						}

						if (connectionState == ConnectionState.STATUS && p.id == StatusResponsePacket.packetId) {
							StatusResponsePacket status = p.convert(new StatusResponsePacket());
							status.value = status.value.replace("\"text\":\"", "\"text\":\"§2Proxy: §r");
							clientOut.writePacket(status);

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
										connectionState = ConnectionState.STATUS;
									}
								}
								break;
							case STATUS:
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
