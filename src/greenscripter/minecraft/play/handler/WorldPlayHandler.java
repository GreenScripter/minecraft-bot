package greenscripter.minecraft.play.handler;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.BlockStates;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.AckChunksPacket;
import greenscripter.minecraft.packet.s2c.play.blocks.BlockEntityDataPacket;
import greenscripter.minecraft.packet.s2c.play.blocks.BlockUpdatePacket;
import greenscripter.minecraft.packet.s2c.play.blocks.ChunkBatchFinishPacket;
import greenscripter.minecraft.packet.s2c.play.blocks.ChunkDataPacket;
import greenscripter.minecraft.packet.s2c.play.blocks.ExplosionPacket;
import greenscripter.minecraft.packet.s2c.play.blocks.SectionUpdatePacket;
import greenscripter.minecraft.packet.s2c.play.blocks.UnloadChunkPacket;
import greenscripter.minecraft.packet.s2c.play.self.LoginPlayPacket;
import greenscripter.minecraft.packet.s2c.play.self.RespawnPacket;
import greenscripter.minecraft.play.data.ClientConfigData;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.RegistryData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.utils.DynamicRegistry.RegistryEntry;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.world.BlockEntity;
import greenscripter.minecraft.world.Chunk;
import greenscripter.minecraft.world.ChunkDataDecoder;
import greenscripter.minecraft.world.World;
import greenscripter.minecraft.world.Worlds;

public class WorldPlayHandler extends PlayHandler {

	public Worlds worlds;

	int chunkDataId = new ChunkDataPacket().id();
	int respawnId = new RespawnPacket().id();
	int loginPlayId = new LoginPlayPacket().id();
	int unloadChunkId = new UnloadChunkPacket().id();
	int explosionId = new ExplosionPacket().id();
	int blockUpdateId = new BlockUpdatePacket().id();
	int sectionUpdateId = new SectionUpdatePacket().id();
	int blockEntityDataId = new BlockEntityDataPacket().id();

	public List<ChunkFirstLoadListener> chunkLoadListeners = new ArrayList<>();
	public List<ChunkUnloadListener> chunkUnloadListeners = new ArrayList<>();
	public List<BlockChangeListener> blockChangeListeners = new ArrayList<>();

	public WorldPlayHandler() {
		worlds = new Worlds(this);
	}

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		WorldData worldData = sc.getData(WorldData.class);
		synchronized (worlds) {
			if (p.id == respawnId) {
				RespawnPacket respawn = p.convert(new RespawnPacket());
				PositionData pos = sc.getData(PositionData.class);
				for (Chunk c : new ArrayList<>(worldData.world.chunks.values())) {
					worldData.world.unloadChunk(c, sc);
				}
				pos.dimension = respawn.dimensionName;

				worldData.world = worlds.getWorld(respawn.dimensionName);
				if (worldData.world == null) {
					RegistryData registryData = sc.getData(RegistryData.class);
					RegistryEntry ent = registryData.getRegistry("minecraft:dimension_type").get(respawn.dimensionType);

					World world = new World();
					world.id = respawn.dimensionName;
					world.dimensionType = ent.entryId;
					world.worlds = worlds;

					world.height = ent.data.asCompound().get("height").asInt().value;
					world.min_y = ent.data.asCompound().get("min_y").asInt().value;

					worlds.worlds.put(world.id, world);

					worldData.world = world;
				}

			} else if (p.id == ChunkBatchFinishPacket.packetId) {
				AckChunksPacket ack = new AckChunksPacket();
				if (sc.getData(ClientConfigData.class).viewDistance <= 1) {
					ack.chunksPerTick = 0.01f;
				} else {
					ack.chunksPerTick = 10;
				}
				sc.sendPacket(ack);
			} else if (p.id == loginPlayId) {
				LoginPlayPacket respawn = p.convert(new LoginPlayPacket());
				PositionData pos = sc.getData(PositionData.class);
				pos.dimension = respawn.dimensionName;

				worldData.world = worlds.getWorld(respawn.dimensionName);
				if (worldData.world == null) {
					RegistryData registryData = sc.getData(RegistryData.class);
					RegistryEntry ent = registryData.getRegistry("minecraft:dimension_type").get(respawn.dimensionType);

					World world = new World();
					world.id = respawn.dimensionName;
					System.out.println("Loaded world " + world.id);
					world.dimensionType = ent.entryId;
					world.worlds = worlds;

					world.height = ent.data.asCompound().get("height").asInt().value;
					world.min_y = ent.data.asCompound().get("min_y").asInt().value;

					worlds.worlds.put(world.id, world);

					worldData.world = world;
				}

			} else if (p.id == chunkDataId) {
				int x = ChunkDataPacket.readXCoordinate(p);
				int z = ChunkDataPacket.readZCoordinate(p);

				if (worldData.world != null) {
					if (worldData.world.isChunkLoaded(x, z)) {
						//					System.out.println("Chunk " + chunk.chunkX + " " + chunk.chunkZ + " already loaded");
						worldData.world.addChunkLoader(worldData.world.getChunk(x, z), sc);
					} else {
						//					System.out.println("Making new Chunk");
						ChunkDataPacket chunk = p.convert(new ChunkDataPacket());
						//					System.out.println("Loading chunk " + chunk.chunkX + " " + chunk.chunkZ + "");
						Chunk c = new Chunk(chunk.chunkX, chunk.chunkZ, worldData.world.min_y, worldData.world.height, worldData.world);

						ChunkDataDecoder.decode(c, chunk.data);

						for (ChunkDataPacket.BlockEntity e : chunk.blockEntities) {
							BlockEntity en = new BlockEntity();
							en.pos = new Position(e.xinchunk + chunk.chunkX * 16, e.y, e.zinchunk + chunk.chunkZ * 16);
							c.addBlockEntity(en);
						}

						worldData.world.addChunkLoader(c, sc);
						for (ChunkFirstLoadListener listener : chunkLoadListeners) {
							listener.chunkLoaded(sc, c);
						}
					}
				}
			} else if (p.id == unloadChunkId) {
				UnloadChunkPacket chunkunload = p.convert(new UnloadChunkPacket());
				Chunk chunk = worldData.world.getChunk(chunkunload.x, chunkunload.z);
				if (chunk != null) {
					worldData.world.unloadChunk(chunk, sc);
					if (chunk.players.isEmpty()) {
						worlds.chunkUnloaded(chunk.world);
						for (ChunkUnloadListener listener : chunkUnloadListeners) {
							listener.chunkUnloaded(sc, chunk);
						}
					}
				}
			} else if (p.id == explosionId) {
				ExplosionPacket explosion = p.convert(new ExplosionPacket());
				int air = BlockStates.getDefaultBlockState("minecraft:air").id();
				if (explosion.blockInteraction != 0) {
					for (Position pos : explosion.blocks) {
						//					sc.out.writePacket(new ExecuteCommandPacket("setblock " + pos.x + " " + pos.y + " " + pos.z + " minecraft:red_stained_glass"));
						//					sc.out.writePacket(new ExecuteCommandPacket("setblock " + pos.x + " " + pos.y + " " + pos.z + " " + BlockStates.getState(worldState.world.getBlock(pos.x, pos.y, pos.z)).format()));
						if (worldData.world.getBlock(pos.x, pos.y, pos.z) != air) {
							worldData.world.setBlock(pos.x, pos.y, pos.z, air);
							for (BlockChangeListener listener : blockChangeListeners) {
								listener.blockChanged(sc, worldData.world, pos.x, pos.y, pos.z, air);
							}
						}

					}
				}

			} else if (p.id == blockUpdateId) {
				BlockUpdatePacket update = p.convert(new BlockUpdatePacket());
				//			sc.out.writePacket(new ExecuteCommandPacket("setblock " + update.pos.x + " " + update.pos.y + " " + update.pos.z + " " + BlockStates.getState(worldState.world.getBlock(update.pos.x, update.pos.y, update.pos.z)).format()));
				if (worldData.world.getBlock(update.pos.x, update.pos.y, update.pos.z) != update.state) {
					worldData.world.setBlock(update.pos.x, update.pos.y, update.pos.z, update.state);
					for (BlockChangeListener listener : blockChangeListeners) {
						listener.blockChanged(sc, worldData.world, update.pos.x, update.pos.y, update.pos.z, update.state);
					}
				}
				//			sc.out.writePacket(new ExecuteCommandPacket("setblock " + update.pos.x + " " + update.pos.y + " " + update.pos.z + " minecraft:green_stained_glass"));

			} else if (p.id == sectionUpdateId) {
				SectionUpdatePacket update = p.convert(new SectionUpdatePacket());
				Chunk chunk = worldData.world.getChunk(update.sectionX, update.sectionZ);
				if (chunk != null) {
					int sectionBlockX = update.sectionX * 16;
					int sectionBlockY = update.sectionY * 16;
					int sectionBlockZ = update.sectionZ * 16;
					for (int i = 0; i < update.ids.length; i++) {
						//					sc.out.writePacket(new ExecuteCommandPacket("setblock " + (update.xs[i] + chunk.chunkX * 16) + " " + (update.ys[i] + update.sectionY * 16) + " " + (update.zs[i] + chunk.chunkZ * 16) + " minecraft:blue_stained_glass"));
						//					int blockWas = chunk.getBlockInChunk(update.xs[i], update.ys[i] + update.sectionY * 16 - chunk.min_y, update.zs[i]);
						//					sc.out.writePacket(new ExecuteCommandPacket("setblock " + (update.xs[i] + chunk.chunkX * 16) + " " + (update.ys[i] + update.sectionY * 16) + " " + (update.zs[i] + chunk.chunkZ * 16) + " " + BlockStates.getState(blockWas).format()));

						if (chunk.getBlockInChunk(update.xs[i], update.ys[i] + sectionBlockY - chunk.min_y, update.zs[i]) != update.ids[i]) {
							chunk.setBlockInChunk(update.xs[i], update.ys[i] + sectionBlockY - chunk.min_y, update.zs[i], update.ids[i]);
							for (BlockChangeListener listener : blockChangeListeners) {
								listener.blockChanged(sc, worldData.world, update.xs[i] + sectionBlockX, update.ys[i] + sectionBlockY, update.zs[i] + sectionBlockZ, update.ids[i]);
							}
						}

					}
				}
			} else if (p.id == blockEntityDataId) {
				BlockEntityDataPacket update = p.convert(new BlockEntityDataPacket());
				BlockEntity en = new BlockEntity();
				en.data = update.nbt;
				en.pos = update.pos;
				en.type = update.type;
				worldData.world.addBlockEntity(en);
				//			System.out.println("Added " + en.pos + " " + en.type + " " + en.data);
			}
		}
	}

	public List<Integer> handlesPackets() {//needs to handle respawn, chunk data, section update and block updates
		return List.of(chunkDataId, //
				respawnId, //
				loginPlayId, //
				unloadChunkId, //
				explosionId, //
				blockUpdateId, //
				sectionUpdateId, //
				blockEntityDataId, //
				ChunkBatchFinishPacket.packetId);
	}

	public void handleDisconnect(ServerConnection sc) {
		WorldData worldData = sc.getData(WorldData.class);
		if (worldData.world != null) {
			synchronized (worlds) {
				for (Chunk c : new ArrayList<>(worldData.world.chunks.values())) {
					worldData.world.unloadChunk(c, sc);
				}
			}
		}

	}

	public static interface ChunkFirstLoadListener {

		public void chunkLoaded(ServerConnection sc, Chunk chunk) throws IOException;
	}

	public static interface ChunkUnloadListener {

		public void chunkUnloaded(ServerConnection sc, Chunk chunk) throws IOException;
	}

	public static interface BlockChangeListener {

		public void blockChanged(ServerConnection sc, World world, int x, int y, int z, int state) throws IOException;
	}
}
