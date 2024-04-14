package greenscripter.minecraft.world;

import java.util.HashMap;
import java.util.Map;

import greenscripter.minecraft.ServerConnection;

public class World {

	public int min_y;
	public int height;
	public String dimensionType;
	public String id;
	public Worlds worlds;

	Map<Long, Chunk> chunks = new HashMap<>();

	public boolean isChunkLoaded(int x, int z) {
		return chunks.containsKey(Chunk.mergeCoords(x, z));
	}

	public boolean isBlockLoaded(int x, int z) {
		return isChunkLoaded(x >> 4, z >> 4);
	}

	public Chunk getChunk(int x, int z) {
		return chunks.get(Chunk.mergeCoords(x, z));
	}

	public Chunk getBlockChunk(int x, int z) {
		return getChunk(x >> 4, z >> 4);
	}

	public int getBlock(int x, int y, int z) {
		Chunk c = getBlockChunk(x, z);
		if (c != null) {
			return c.getBlock(x, y, z);
		} else {
			return -1;
		}
	}

	public boolean isPassible(int x, int y, int z, boolean[] noCollides) {
		int block = getBlock(x, y, z);
		if (block == -2) {
			return true;
		}
		if (block == -1) {
			return false;
		}
		return noCollides[block];
	}

	public boolean isPassiblePlayer(int x, int y, int z, boolean[] noCollides) {
		return isPassible(x, y, z, noCollides) && isPassible(x, y + 1, z, noCollides);
	}

	public void setBlock(int x, int y, int z, int block) {
		Chunk c = getBlockChunk(x, z);
		if (c != null) {
			c.setBlock(x, y, z, block);
		}
	}

	public void addChunkLoader(Chunk c, ServerConnection sc) {
		chunks.put(Chunk.mergeCoords(c.chunkX, c.chunkZ), c);
		c.players.add(sc);
	}

	public void unloadChunk(Chunk c, ServerConnection sc) {
		c.players.remove(sc);
		if (c.players.size() == 0) {
			chunks.remove(Chunk.mergeCoords(c.chunkX, c.chunkZ));
		}
	}

	public String toString() {
		return "World " + id + " " + chunks.values();
	}
}
