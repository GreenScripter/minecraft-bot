package greenscripter.minecraft.world;

import java.util.HashSet;
import java.util.Set;

import greenscripter.minecraft.ServerConnection;

public class Chunk {

	public int chunkX;
	public int chunkZ;
	public int min_y;
	public int height;
	public World world;
	public Set<ServerConnection> players = new HashSet<>();

	int[][][] blocks;//stored in y,z,x order to match chunk data from the server.

	public Chunk(int chunkX, int chunkZ, int minY, int height, World world) {
		blocks = new int[height][16][16];
		this.min_y = minY;
		this.height = height;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.world = world;
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
		return ((0xFFFFFFFFl & x) << 32) | (0xFFFFFFFFl & z);
	}

	public String toString() {
		return "Chunk " + chunkX + ", " + chunkZ + " " + players;
	}

}
