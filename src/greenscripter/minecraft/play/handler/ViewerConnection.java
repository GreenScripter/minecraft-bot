package greenscripter.minecraft.play.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.io.IOException;
import java.net.Socket;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.ServerConnection.ConnectionState;
import greenscripter.minecraft.nbt.NBTTagCompound;
import greenscripter.minecraft.nbt.NBTTagString;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.configuration.AckFinishConfigPacket;
import greenscripter.minecraft.packet.c2s.configuration.ClientKnownPacksConfigPacket;
import greenscripter.minecraft.packet.c2s.handshake.HandshakePacket;
import greenscripter.minecraft.packet.c2s.login.LoginAcknowledgePacket;
import greenscripter.minecraft.packet.c2s.login.LoginStartPacket;
import greenscripter.minecraft.packet.c2s.play.ExecuteCommandPacket;
import greenscripter.minecraft.packet.c2s.play.ExecuteCommandSignedPacket;
import greenscripter.minecraft.packet.c2s.play.KeepAliveReplyPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionRotationPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMoveRotationPacket;
import greenscripter.minecraft.packet.c2s.play.TeleportConfirmPacket;
import greenscripter.minecraft.packet.c2s.play.inventory.ClickContainerPacket;
import greenscripter.minecraft.packet.c2s.play.inventory.CloseContainerPacket;
import greenscripter.minecraft.packet.c2s.play.inventory.HotbarSlotPacket;
import greenscripter.minecraft.packet.s2c.configuration.FinishConfigPacket;
import greenscripter.minecraft.packet.s2c.configuration.RegistryConfigPacket;
import greenscripter.minecraft.packet.s2c.configuration.ServerKnownPacksConfigPacket;
import greenscripter.minecraft.packet.s2c.login.LoginSuccessPacket;
import greenscripter.minecraft.packet.s2c.login.SetCompressionPacket;
import greenscripter.minecraft.packet.s2c.play.GameEventPacket;
import greenscripter.minecraft.packet.s2c.play.KeepAlivePacket;
import greenscripter.minecraft.packet.s2c.play.SystemChatPacket;
import greenscripter.minecraft.packet.s2c.play.blocks.ChunkBatchFinishPacket;
import greenscripter.minecraft.packet.s2c.play.blocks.ChunkBatchStartPacket;
import greenscripter.minecraft.packet.s2c.play.blocks.ChunkDataPacket;
import greenscripter.minecraft.packet.s2c.play.inventory.OpenContainerPacket;
import greenscripter.minecraft.packet.s2c.play.inventory.SetContainerContentPacket;
import greenscripter.minecraft.packet.s2c.play.inventory.SetHeldItemPacket;
import greenscripter.minecraft.packet.s2c.play.self.RespawnPacket;
import greenscripter.minecraft.packet.s2c.play.self.SetHealthPacket;
import greenscripter.minecraft.packet.s2c.play.self.TeleportRequestPacket;
import greenscripter.minecraft.play.data.InventoryData;
import greenscripter.minecraft.play.data.PlayerData;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.RegistryData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.handler.ViewerTrackPlayHandler.ViewerTrackPlayData;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.Chunk;
import greenscripter.minecraft.world.ChunkDataEncoder;

public class ViewerConnection extends PlayHandler {

	Socket client;
	MCInputStream clientIn;
	MCOutputStream clientOut;

	ServerConnection linked;

	List<Integer> handlesPackets;
	ConnectionState connectionState = ConnectionState.HANDSHAKE;

	Set<Integer> awaitingTps = new HashSet<>();

	public ViewerConnection(ServerConnection linkTo, Socket client) throws IOException {
		handlesPackets = new ArrayList<>();
		for (int i = 0; i < 200; i++) {
			handlesPackets.add(i);
		}
		this.linked = linkTo;

		clientIn = new MCInputStream(client.getInputStream());
		clientOut = new MCOutputStream(client.getOutputStream());

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
								}
							}
							break;
						case STATUS:
							client.close();
							break;
						case LOGIN:
							if (p.id == LoginStartPacket.packetId) {
								//								LoginStartPacket loginStart = p.convert(new LoginStartPacket());
								SetCompressionPacket compress = new SetCompressionPacket();
								compress.value = 256;
								clientOut.writePacket(compress);

								clientIn.compression = true;
								clientOut.compressionThreshold = compress.value;
								clientOut.actuallyCompress = true;

								LoginSuccessPacket login = new LoginSuccessPacket();
								login.name = linked.name;
								login.uuid = linked.uuid;
								login.properties = 0;
								clientOut.writePacket(login);
								System.out.println("Logged in " + login.name);
							}
							if (p.id == LoginAcknowledgePacket.packetId) {
								connectionState = ConnectionState.CONFIGURATION;
								clientOut.writePacket(new ServerKnownPacksConfigPacket());
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
							if (p.id == KeepAliveReplyPacket.packetId) {
								continue;
							}
							if (p.id == ExecuteCommandPacket.packetId || p.id == ExecuteCommandSignedPacket.packetId) {
								ExecuteCommandPacket command = p.convert(new ExecuteCommandPacket());
								if (runCommand(command.command)) {
									continue;
								}
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
							}
							break;
						default:
							break;

					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				linked.removePlayHandler(this);
			}

		}).start();

		new Thread(() -> {
			try {
				while (true) {
					if (connectionState == ConnectionState.PLAY) {
						clientOut.writePacket(new KeepAlivePacket(System.currentTimeMillis()));
					}
					Thread.sleep(10000);
				}
			} catch (Exception e) {
				//				e.printStackTrace();
			}

		}).start();
	}

	boolean spawned = false;
	int previousPlayerId = 0;
	ServerConnection previousLink = null;

	private void initUser() throws IOException {
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
		}
		previousLink = linked;
		previousPlayerId = player.entityId;
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

		clientOut.writePacket(new GameEventPacket(GameEventPacket.START_WAITING_FOR_CHUNKS));

		clientOut.writePacket(new ChunkBatchStartPacket());

		Chunk center = world.world.getBlockChunk((int) pos.pos.x, (int) pos.pos.z);

		int sent = 0;
		for (int i = -10; i <= 10; i++) {
			for (int j = -10; j <= 10; j++) {
				Chunk send = world.world.getChunk(center.chunkX + i, center.chunkZ + j);
				if (send == null) continue;
				ChunkDataPacket loadChunk = new ChunkDataPacket();
				loadChunk.chunkX = send.chunkX;
				loadChunk.chunkZ = send.chunkZ;
				loadChunk.useBlockEntities(send.blockEntities.values());
				loadChunk.heightmap = new NBTTagCompound();
				loadChunk.data = ChunkDataEncoder.encode(send);
				clientOut.writePacket(loadChunk);
				sent++;
			}
		}

		clientOut.writePacket(new ChunkBatchFinishPacket(sent));
		clientOut.writePacket(new SetHealthPacket(player.health, player.food, player.saturation));
		clientOut.writePacket(new SetContainerContentPacket(inv.inv));
		if (inv.screen != null) {
			clientOut.writePacket(new OpenContainerPacket(inv.screen));
			clientOut.writePacket(new SetContainerContentPacket(inv.screen));
		}
		clientOut.writePacket(new SetHeldItemPacket((byte) inv.hotbarSlot));

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
	}

	public void writePacket(Packet p) {
		try {
			clientOut.writePacket(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean runCommand(String command) {
		if (command.startsWith("pov ")) {
			String target = command.substring(4);
			ViewerTrackPlayData tracked = linked.getData(ViewerTrackPlayData.class);

			var controller = tracked.controller;
			for (ServerConnection sc : controller.getAlive()) {
				if (sc.name.equalsIgnoreCase(target)) {
					synchronized (linked) {
						linked.removePlayHandler(this);
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
					//					PlayerData player = linked.getData(PlayerData.class);
					//
					//					writePacket(new DeathPacket(player.entityId, new NBTTagString("")));

					return true;
				}
			}
			SystemChatPacket reply = new SystemChatPacket();
			reply.content = new NBTTagString("§cUnable to view " + target);
			writePacket(reply);
			return true;
		}
		return false;
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

	public boolean handlesTick() {
		return super.handlesTick();
	}

	public List<Integer> handlesPackets() {
		return handlesPackets;
	}
}
