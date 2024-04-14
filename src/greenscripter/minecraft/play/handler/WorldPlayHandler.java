package greenscripter.minecraft.play.handler;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.BlockStates;
import greenscripter.minecraft.nbt.NBTTagCompound;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.AckChunksPacket;
import greenscripter.minecraft.packet.s2c.play.BlockUpdatePacket;
import greenscripter.minecraft.packet.s2c.play.ChunkDataPacket;
import greenscripter.minecraft.packet.s2c.play.ExplosionPacket;
import greenscripter.minecraft.packet.s2c.play.LoginPlayPacket;
import greenscripter.minecraft.packet.s2c.play.RespawnPacket;
import greenscripter.minecraft.packet.s2c.play.SectionUpdatePacket;
import greenscripter.minecraft.packet.s2c.play.UnloadChunkPacket;
import greenscripter.minecraft.play.state.PositionState;
import greenscripter.minecraft.play.state.RegistryState;
import greenscripter.minecraft.play.state.WorldState;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.world.Chunk;
import greenscripter.minecraft.world.ChunkDataDecoder;
import greenscripter.minecraft.world.World;
import greenscripter.minecraft.world.Worlds;

public class WorldPlayHandler extends PlayHandler {

	public Worlds worlds = new Worlds();

	int chunkDataId = new ChunkDataPacket().id();
	int respawnId = new RespawnPacket().id();
	int loginPlayId = new LoginPlayPacket().id();
	int unloadChunkId = new UnloadChunkPacket().id();
	int explosionId = new ExplosionPacket().id();
	int blockUpdateId = new BlockUpdatePacket().id();
	int sectionUpdateId = new SectionUpdatePacket().id();

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		WorldState worldState = sc.getState(WorldState.class);

		if (p.id == respawnId) {
			RespawnPacket respawn = p.convert(new RespawnPacket());
			PositionState pos = sc.getState(PositionState.class);
			for (Chunk c : new ArrayList<>(worldState.world.chunks.values())) {
				worldState.world.unloadChunk(c, sc);
			}
			pos.dimension = respawn.dimensionName;

			worldState.world = worlds.getWorld(respawn.dimensionName);
			if (worldState.world == null) {
				World world = new World();
				world.id = respawn.dimensionName;
				world.dimensionType = respawn.dimensionType;
				world.worlds = worlds;

				RegistryState registryState = sc.getState(RegistryState.class);
				var dimensionTypeList = registryState.configuredRegistry.get("minecraft:dimension_type").asCompound().get("value").asList(NBTTagCompound.class);
				NBTTagCompound type = null;
				for (NBTTagCompound c : dimensionTypeList.value) {
					if (c.get("name").asString().value.equals(world.dimensionType)) {
						type = c;
						break;
					}
				}
				world.height = type.get("height").asInt().value;
				world.min_y = type.get("min_y").asInt().value;

				worlds.worlds.put(world.id, world);

				worldState.world = world;
			}

		} else if (p.id == loginPlayId) {
			LoginPlayPacket respawn = p.convert(new LoginPlayPacket());
			PositionState pos = sc.getState(PositionState.class);
			pos.dimension = respawn.dimensionName;

			worldState.world = worlds.getWorld(respawn.dimensionName);
			if (worldState.world == null) {
				World world = new World();
				world.id = respawn.dimensionName;
				System.out.println("Loaded world " + world.id);
				world.dimensionType = respawn.dimensionType;
				world.worlds = worlds;

				RegistryState registryState = sc.getState(RegistryState.class);
				var dimensionTypeList = registryState.configuredRegistry.get("minecraft:dimension_type").asCompound().get("value").asList(NBTTagCompound.class);
				NBTTagCompound type = null;
				for (NBTTagCompound c : dimensionTypeList.value) {
					if (c.get("name").asString().value.equals(world.dimensionType)) {
						type = c.get("element").asCompound();
						break;
					}
				}
				world.height = type.get("height").asInt().value;
				world.min_y = type.get("min_y").asInt().value;

				worlds.worlds.put(world.id, world);

				worldState.world = world;
			}

		} else if (p.id == chunkDataId) {
			ChunkDataPacket chunk = p.convert(new ChunkDataPacket());
			if (worldState.world != null) {
				if (worldState.world.isChunkLoaded(chunk.chunkX, chunk.chunkZ)) {
					//					System.out.println("Chunk " + chunk.chunkX + " " + chunk.chunkZ + " already loaded");
					worldState.world.addChunkLoader(worldState.world.getChunk(chunk.chunkX, chunk.chunkZ), sc);
				} else {
					//					System.out.println("Loading chunk " + chunk.chunkX + " " + chunk.chunkZ + "");
					Chunk c = new Chunk(chunk.chunkX, chunk.chunkZ, worldState.world.min_y, worldState.world.height, worldState.world);
					worldState.world.addChunkLoader(c, sc);

					ChunkDataDecoder.decode(c, chunk.data);
				}
			}
			sc.out.writePacket(new AckChunksPacket());
		} else if (p.id == unloadChunkId) {
			UnloadChunkPacket chunkunload = p.convert(new UnloadChunkPacket());
			Chunk chunk = worldState.world.getChunk(chunkunload.x, chunkunload.z);
			if (chunk != null) {
				worldState.world.unloadChunk(chunk, sc);
			}
		} else if (p.id == explosionId) {
			ExplosionPacket explosion = p.convert(new ExplosionPacket());
			int air = BlockStates.getDefaultBlockState("minecraft:air").id();
			if (explosion.blockInteraction != 0) {
				for (Position pos : explosion.blocks) {
					//					sc.out.writePacket(new ExecuteCommandPacket("setblock " + pos.x + " " + pos.y + " " + pos.z + " minecraft:red_stained_glass"));
					//					sc.out.writePacket(new ExecuteCommandPacket("setblock " + pos.x + " " + pos.y + " " + pos.z + " " + BlockStates.getState(worldState.world.getBlock(pos.x, pos.y, pos.z)).format()));
					worldState.world.setBlock(pos.x, pos.y, pos.z, air);

				}
			}

		} else if (p.id == blockUpdateId) {
			BlockUpdatePacket update = p.convert(new BlockUpdatePacket());
			//			sc.out.writePacket(new ExecuteCommandPacket("setblock " + update.pos.x + " " + update.pos.y + " " + update.pos.z + " " + BlockStates.getState(worldState.world.getBlock(update.pos.x, update.pos.y, update.pos.z)).format()));

			worldState.world.setBlock(update.pos.x, update.pos.y, update.pos.z, update.state);
			//			sc.out.writePacket(new ExecuteCommandPacket("setblock " + update.pos.x + " " + update.pos.y + " " + update.pos.z + " minecraft:green_stained_glass"));

		} else if (p.id == sectionUpdateId) {
			SectionUpdatePacket update = p.convert(new SectionUpdatePacket());
			Chunk chunk = worldState.world.getChunk(update.sectionX, update.sectionZ);
			if (chunk != null) {
				for (int i = 0; i < update.ids.length; i++) {
					//					sc.out.writePacket(new ExecuteCommandPacket("setblock " + (update.xs[i] + chunk.chunkX * 16) + " " + (update.ys[i] + update.sectionY * 16) + " " + (update.zs[i] + chunk.chunkZ * 16) + " minecraft:blue_stained_glass"));
					//					int blockWas = chunk.getBlockInChunk(update.xs[i], update.ys[i] + update.sectionY * 16 - chunk.min_y, update.zs[i]);
					//					sc.out.writePacket(new ExecuteCommandPacket("setblock " + (update.xs[i] + chunk.chunkX * 16) + " " + (update.ys[i] + update.sectionY * 16) + " " + (update.zs[i] + chunk.chunkZ * 16) + " " + BlockStates.getState(blockWas).format()));

					chunk.setBlockInChunk(update.xs[i], update.ys[i] + update.sectionY * 16 - chunk.min_y, update.zs[i], update.ids[i]);

				}
			}
		}
	}

	public List<Integer> handlesPackets() {//needs to handle respawn, chunk data, section update and block updates
		return List.of(chunkDataId, respawnId, loginPlayId, unloadChunkId, explosionId, blockUpdateId, sectionUpdateId);
	}
}
