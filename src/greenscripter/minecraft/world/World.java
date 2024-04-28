package greenscripter.minecraft.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.BlockStates;
import greenscripter.minecraft.gameinfo.BlockStates.BlockState;
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

	private boolean[] extraTall = BlockStates.getBlockSet();
	{
		extraTall = BlockStates.addTagToBlockSet(extraTall, "minecraft:fences");
		extraTall = BlockStates.addTagToBlockSet(extraTall, "minecraft:fence_gates");
		extraTall = BlockStates.addTagToBlockSet(extraTall, "minecraft:walls");
		extraTall = BlockStates.addToBlockSet(extraTall, "minecraft:cactus");
		extraTall = BlockStates.addToBlockSet(extraTall, "minecraft:magma_block");
		for (BlockState s : BlockStates.getBlockStates("minecraft:campfire")) {
			if ("true".equals(s.properties().get("lit"))) {
				extraTall[s.id()] = true;
			}
		}
		for (BlockState s : BlockStates.getBlockStates("minecraft:soul_campfire")) {
			if ("true".equals(s.properties().get("lit"))) {
				extraTall[s.id()] = true;
			}
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

	public boolean isColliding(int x, int y, int z, boolean[] collides) {
		int block = getBlock(x, y, z);
		if (block == -2) {
			return false;
		}
		if (block == -1) {
			return true;
		}
		return collides[block];
	}

	public boolean isPassiblePlayer(Position pos, boolean[] noCollides) {
		return isPassible(pos.x, pos.y, pos.z, noCollides) && isPassible(pos.x, pos.y + 1, pos.z, noCollides) && !isColliding(pos.x, pos.y - 1, pos.z, extraTall);
	}

	public boolean isPassiblePlayer(int x, int y, int z, boolean[] noCollides) {
		return isPassible(x, y, z, noCollides) && isPassible(x, y + 1, z, noCollides) && !isColliding(x, y - 1, z, extraTall);
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
		long coord = Chunk.mergeCoords(c.chunkX, c.chunkZ);
		Chunk exists = chunks.get(coord);
		if (exists == null) {
			chunks.put(coord, c);
			exists = c;
		}
		if (c != exists) {
			System.out.println("Chunk " + c.chunkX + " " + c.chunkZ + " has wrong loaders.");
			System.out.println(c);
			System.out.println(exists);
		}
		exists.players.add(sc);
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
