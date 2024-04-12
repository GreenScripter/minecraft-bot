package greenscripter.minecraft.world;

import java.util.HashSet;
import java.util.Set;

import greenscripter.minecraft.ServerConnection;

public class Chunk {

	int chunkX;
	int chunkZ;
	int min_y;
	int height;
	World world;
	Set<ServerConnection> players = new HashSet<>();

	int[][][] blocks;

	public Chunk(int chunkX, int chunkZ, int minY, int height, World world) {
		blocks = new int[16][height][16];
		this.min_y = minY;
		this.height = height;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.world = world;
	}

	public int getBlock(int x, int y, int z) {
		return blocks[x - (chunkX << 4)][y - min_y][z - (chunkZ << 4)];
	}

	public void setBlock(int x, int y, int z, int block) {
		blocks[x - (chunkX << 4)][y - min_y][z - (chunkZ << 4)] = block;
	}

	public int getBlockInChunk(int x, int y, int z) {
		return blocks[x][y][z];
	}

	public void setBlockInChunk(int x, int y, int z, int block) {
		blocks[x][y][z] = block;
	}

	public static long mergeCoords(int x, int z) {
		return ((0xFFFFFFFFl & x) << 32) | (0xFFFFFFFFl & z);
	}

	public String toString() {
		return "Chunk " + chunkX + ", " + chunkZ + " " + players;
	}

}
