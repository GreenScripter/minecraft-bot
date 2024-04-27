package greenscripter.minecraft.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.play.handler.WorldPlayHandler.BlockChangeListener;
import greenscripter.minecraft.play.handler.WorldPlayHandler.ChunkFirstLoadListener;
import greenscripter.minecraft.play.handler.WorldPlayHandler.ChunkUnloadListener;
import greenscripter.minecraft.utils.BlockBox;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.utils.Vector;
import greenscripter.remoteindicators.IndicatorServer;

public class WorldSearch {

	public Set<Long> foundBlocks = Collections.synchronizedSet(new HashSet<>());
	public Worlds worlds;
	public boolean[] searchTargets;

	public boolean activeUpdates = false;
	public boolean searchNew = true;
	public List<SearchResult> results = Collections.synchronizedList(new ArrayList<>());
	public IndicatorServer render;

	private BlockChangeListener bcl;
	private ChunkFirstLoadListener cfll;
	private ChunkUnloadListener cul;

	Set<ServerConnection> using = new HashSet<>();

	public WorldSearch(Worlds worlds, boolean[] searchTargets, boolean active) {
		this.worlds = worlds;
		this.searchTargets = searchTargets;
		activeUpdates = active;

	}

	public void activate() {
		if (searchNew) worlds.handler.chunkLoadListeners.add(cfll = (sc, c) -> {
			searchChunk(c);
		});

		worlds.handler.chunkUnloadListeners.add(cul = (sc, c) -> {
			clearChunk(c);
		});
		if (activeUpdates) worlds.handler.blockChangeListeners.add(bcl = (sc, w, x, y, z, s) -> {
			addBlock(w, x, y, z, s);
		});
	}

	public void addBlock(World w, int x, int y, int z, int state) {
		if (searchTargets[state]) {
			SearchResult tree = new SearchResult(w, new Position(x, y, z));
			tree.chunk = Chunk.mergeCoords(x / 16, z / 16);
			tree.dimension = w.id;
			if (!tree.blocks.isEmpty()) {
				results.add(tree);
				if (render != null) {
					tree.renderId = render.addCuboid(w.id, new Vector(tree.boundingBox.pos1).add(-0.5, 0, -0.5), new Vector(tree.boundingBox.pos2.copy()).add(0.5, 1, 0.5), IndicatorServer.getColor(0, 255, 255, 255));
				}
			}
		}
	}

	public void clearChunk(Chunk c) {
		long chunk = Chunk.mergeCoords(c.chunkX, c.chunkZ);

		results.removeIf(t -> {
			if (t.chunk == chunk) {
				t.blocks.forEach(p -> foundBlocks.remove(p.getEncoded()));
				if (render != null) render.removeShape(t.renderId);
				return true;
			}
			return false;
		});

	}

	public void deactivate() {
		worlds.handler.chunkLoadListeners.remove(cfll);
		worlds.handler.chunkUnloadListeners.remove(cul);
		worlds.handler.blockChangeListeners.remove(bcl);
	}

	public void searchChunk(Chunk c) {
		for (int y = c.height - 1; y >= 0; y--) {
			for (int z = 0; z < 16; z++) {
				for (int x = 0; x < 16; x++) {
					int block = c.blocks[y][z][x];//c.getBlockInChunk(x, y, z);
					if (searchTargets[block]) {
						SearchResult result = new SearchResult(c.world, new Position(x + c.chunkX * 16, y + c.min_y, z + c.chunkZ * 16));
						result.chunk = Chunk.mergeCoords(c.chunkX, c.chunkZ);
						result.dimension = c.world.id;
						if (!result.blocks.isEmpty()) {
							results.add(result);
							if (render != null) {
								result.renderId = render.addCuboid(c.world.id, new Vector(result.boundingBox.pos1).add(-0.5, 0, -0.5), new Vector(result.boundingBox.pos2.copy()).add(0.5, 1, 0.5), IndicatorServer.getColor(0, 255, 255, 255));
							}
						}
					}
				}
			}
		}

	}

	public void performInitialSearch() {
		for (var worlde : worlds.worlds.values()) {
			for (var entry : worlde.chunks.values()) {
				searchChunk(entry);
			}
		}
	}

	public class SearchResult {

		public long chunk;
		public List<Position> blocks = new ArrayList<>();
		public BlockBox boundingBox;
		public int renderId;
		public String dimension;

		public SearchResult(World w, Position start) {
			List<Position> queue = new ArrayList<>();
			queue.add(start.copy());
			while (!queue.isEmpty()) {
				Position next = queue.remove(queue.size() - 1);
				int block = w.getBlock(next);
				if (block >= 0 && searchTargets[block]) {
					if (foundBlocks.add(next.getEncoded())) {
						blocks.add(next);
						for (int x = -1; x <= 1; x++) {
							for (int y = -1; y <= 1; y++) {
								for (int z = -1; z <= 1; z++) {
									if (x == y && y == z && x == 0) continue;
									Position add = next.copy().add(x, y, z);
									if (add.x >> 4 == next.x >> 4 && add.z >> 4 == next.z >> 4) {
										queue.add(add);
									}
								}
							}
						}
					}
				}
			}
			boundingBox = new BlockBox(blocks);
		}

	}
}
