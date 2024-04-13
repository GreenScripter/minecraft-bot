package greenscripter.minecraft.play.handler;

import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.nbt.NBTTagCompound;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.AckChunksPacket;
import greenscripter.minecraft.packet.s2c.play.ChunkDataPacket;
import greenscripter.minecraft.packet.s2c.play.LoginPlayPacket;
import greenscripter.minecraft.packet.s2c.play.RespawnPacket;
import greenscripter.minecraft.play.state.PositionState;
import greenscripter.minecraft.play.state.RegistryState;
import greenscripter.minecraft.play.state.WorldState;
import greenscripter.minecraft.world.Chunk;
import greenscripter.minecraft.world.ChunkDataDecoder;
import greenscripter.minecraft.world.World;
import greenscripter.minecraft.world.Worlds;

public class WorldPlayHandler extends PlayHandler {

	public Worlds worlds = new Worlds();

	int chunkDataId = new ChunkDataPacket().id();
	int respawnId = new RespawnPacket().id();
	int loginPlayId = new LoginPlayPacket().id();

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		if (p.id == respawnId) {
			RespawnPacket respawn = p.convert(new RespawnPacket());
			PositionState pos = sc.getState(PositionState.class);
			pos.dimension = respawn.dimensionName;

			WorldState worldState = sc.getState(WorldState.class);
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

			WorldState worldState = sc.getState(WorldState.class);
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
			WorldState worldState = sc.getState(WorldState.class);
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
		}
	}

	public List<Integer> handlesPackets() {//needs to handle respawn, chunk data, section update and block updates
		return List.of(chunkDataId, respawnId, loginPlayId);
	}
}
