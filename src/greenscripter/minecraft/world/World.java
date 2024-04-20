package greenscripter.minecraft.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.world.entity.Entity;

public class World {

	public int min_y;
	public int height;
	public String dimensionType;
	public String id;
	public Worlds worlds;

	public Map<Long, Chunk> chunks = new HashMap<>();
	public Map<Integer, Entity> entities = new HashMap<>();

	public boolean isChunkLoaded(int x, int z) {
		return chunks.containsKey(Chunk.mergeCoords(x, z));
	}

	public boolean isBlockLoaded(int x, int z) {
		return isChunkLoaded(x >> 4, z >> 4);
	}

	public Chunk getChunk(int x, int z) {
		return chunks.get(Chunk.mergeCoords(x, z));
	}

	public Entity getEntity(int id) {
		return entities.get(id);
	}

	public Chunk getBlockChunk(int x, int z) {
		return getChunk(x >> 4, z >> 4);
	}

	public int getBlock(Position pos) {
		return getBlock(pos.x, pos.y, pos.z);
	}

	public int getBlock(int x, int y, int z) {
		Chunk c = getBlockChunk(x, z);
		if (c != null) {
			return c.getBlock(x, y, z);
		} else {
			return -1;
		}
	}

	public BlockEntity getBlockEntity(Position pos) {
		return getBlockEntity(pos.x, pos.y, pos.z);
	}

	public BlockEntity getBlockEntity(int x, int y, int z) {
		Chunk c = getBlockChunk(x, z);
		if (c != null) {
			return c.getBlockEntity(x, y, z);
		} else {
			return null;
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

	public List<Position> searchChunk(Chunk chunk, int cx, int targetY, int cz, boolean[] targets, int limit, boolean skip) {
		List<Position> pos = new ArrayList<>();
		int start = Math.max(0, Math.min(chunk.height - 2, targetY - chunk.min_y));
		int downY = start;
		int upY = start + 1;
		for (int yIndex = 0; yIndex < chunk.height; yIndex++) {
			int y = downY;
			if ((yIndex & 1) == 0) {
				y = upY;
				if (upY >= chunk.height) {
					y = downY;
				}
				upY++;
			} else {
				if (downY < 0) {
					y = upY;
				}
				downY--;
			}
			try {
				for (int z = 0; z < 16; z++) {
					for (int x = 0; x < 16; x++) {
						int block = chunk.blocks[y][z][x];
						if (targets[block]) {
							pos.add(new Position(x + (cx << 4), y + chunk.min_y, z + (cz << 4)));
							if (skip && pos.size() > limit) {
								return pos;
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(start + " " + upY + " " + downY + " " + yIndex + " " + y);
			}
		}
		return pos;
	}

	Set<Chunk> searched = new HashSet<>();

	public List<Position> performSearch(int x, int y, int z, boolean[] targets, int limit, int distanceHint, boolean searchOnce) {
		List<Position> blocks = new ArrayList<>();
		List<Chunk> chunks = new ArrayList<>(this.chunks.values());
		chunks = chunks.stream()//
				.filter(c -> Math.abs(c.chunkX * 16 + 8 - x) + Math.abs(c.chunkZ * 16 + 8 - z) <= distanceHint)//
				.sorted((c1, c2) -> Math.abs(c1.chunkX * 16 + 8 - x) + Math.abs(c1.chunkZ * 16 + 8 - z) - (Math.abs(c2.chunkX * 16 + 8 - x) + Math.abs(c2.chunkZ * 16 + 8 - z)))//
				.toList();
		for (Chunk c : chunks) {
			if (searchOnce && searched.contains(c)) continue;
			List<Position> pos = searchChunk(c, c.chunkX, y, c.chunkZ, targets, limit, !searchOnce);
			searched.add(c);
			limit -= pos.size();
			blocks.addAll(pos);

			if (limit <= 0) break;
		}
		return blocks;
	}

	public void setBlock(Position pos, int block) {
		setBlock(pos.x, pos.y, pos.z, block);
	}

	public void setBlock(int x, int y, int z, int block) {
		Chunk c = getBlockChunk(x, z);
		if (c != null) {
			c.setBlock(x, y, z, block);
			c.removeBlockEntity(x, y, z);
		}
	}

	public void setBlockEntity(Position pos, BlockEntity block) {
		setBlockEntity(pos.x, pos.y, pos.z, block);
	}

	public void setBlockEntity(int x, int y, int z, BlockEntity block) {
		Chunk c = getBlockChunk(x, z);
		if (c != null) {
			c.setBlockEntity(x, y, z, block);
		}
	}

	public void addBlockEntity(BlockEntity block) {
		Chunk c = getBlockChunk(block.pos.x, block.pos.z);
		if (c != null) {
			c.setBlockEntity(block.pos.x, block.pos.y, block.pos.z, block);
		}
	}

	public void addChunkLoader(Chunk c, ServerConnection sc) {
		chunks.put(Chunk.mergeCoords(c.chunkX, c.chunkZ), c);
		c.players.add(sc);
	}

	public void unloadChunk(Chunk c, ServerConnection sc) {
		if (c == null) return;
		c.players.remove(sc);
		if (c.players.size() == 0) {
			chunks.remove(Chunk.mergeCoords(c.chunkX, c.chunkZ));
		}
	}

	public void addEntityLoader(Entity e, ServerConnection sc) {
		entities.put(e.entityId, e);
		e.players.add(sc);
	}

	public void unloadEntity(Entity e, ServerConnection sc) {
		if (e == null) return;
		e.players.remove(sc);

		if (e.players.size() == 0) {
			entities.remove(e.entityId);
		} else if (e.maintainer == sc) {
			e.maintainer = e.players.iterator().next();
		}
	}

	public String toString() {
		return "World " + id + " " + chunks.values();
	}
}
