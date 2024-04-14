package greenscripter.minecraft.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.utils.Position;

public class Chunk {

	public int chunkX;
	public int chunkZ;
	public int min_y;
	public int height;
	public World world;
	public Set<ServerConnection> players = new HashSet<>();
	public Map<Position, BlockEntity> blockEntities = new HashMap<>();

	int[][][] blocks;//stored in y,z,x order to match chunk data from the server.

	public Chunk(int chunkX, int chunkZ, int minY, int height, World world) {
		blocks = new int[height][16][16];
		this.min_y = minY;
		this.height = height;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.world = world;
	}

	public BlockEntity getBlockEntity(int x, int y, int z) {
		return blockEntities.get(new Position(x, y, z));
	}

	public void setBlockEntity(int x, int y, int z, BlockEntity e) {
		e.pos = new Position(x, y, z);
		blockEntities.put(e.pos, e);
	}

	public void addBlockEntity(BlockEntity e) {
		blockEntities.put(e.pos, e);
	}

	public void removeBlockEntity(BlockEntity e) {
		blockEntities.remove(e.pos);
	}

	public void removeBlockEntity(int x, int y, int z) {
		blockEntities.remove(new Position(x, y, z));
	}

	public int getBlock(int x, int y, int z) {
		z = z - (chunkZ << 4);
		x = x - (chunkX << 4);
		y = y - min_y;
		if (y < 0 || y >= height || z < 0 || z > 15 || x < 0 || x > 15) {
			return -2;
		}
		return blocks[y][z][x];
	}

	public void setBlock(int x, int y, int z, int block) {
		blocks[y - min_y][z - (chunkZ << 4)][x - (chunkX << 4)] = block;
	}

	public int getBlockInChunk(int x, int y, int z) {
		return blocks[y][z][x];
	}

	public void setBlockInChunk(int x, int y, int z, int block) {
		blocks[y][z][x] = block;
	}

	public static long mergeCoords(int x, int z) {
		return ((0xFFFFFFFFl & z) << 32) | (0xFFFFFFFFl & x);
	}

	public static int getZ(long merged) {
		return (int) (0xFFFFFFFFl & merged);
	}

	public static int getX(long merged) {
		return (int) (merged >>> 32);
	}

	public String toString() {
		return "Chunk " + chunkX + ", " + chunkZ + " " + players;
	}

}
