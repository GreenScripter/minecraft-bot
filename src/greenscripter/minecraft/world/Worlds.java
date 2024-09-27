package greenscripter.minecraft.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.BlockStates;
import greenscripter.minecraft.play.handler.WorldPlayHandler;

public class Worlds {

	public Map<String, World> worlds = new HashMap<>();
	public WorldPlayHandler handler;
	private boolean[] check = BlockStates.getBlockSet();
	public ExecutorService searchThreads = Executors.newFixedThreadPool(2);

	List<WorldSearch> searches = new ArrayList<>();

	public Worlds(WorldPlayHandler handler) {
		this.handler = handler;
		if (handler != null) {
			handler.chunkLoadListeners.add((sc, c) -> {
				if (!searches.isEmpty()) {
					searchThreads.execute(() -> {
						for (int y = c.height - 1; y >= 0; y--) {
							for (int z = 0; z < 16; z++) {
								for (int x = 0; x < 16; x++) {
									int block = c.blocks[y][z][x];//c.getBlockInChunk(x, y, z);
									if (check[block]) {
										synchronized (searches) {
											for (var search : searches) {
												search.addBlock(c.world, x + c.chunkX * 16, y + c.min_y, z + c.chunkZ * 16, block);
											}
										}
									}
								}
							}
						}
						synchronized (worlds) {
							if (c.players.isEmpty()) {
								synchronized (searches) {
									for (var search : searches) {
										search.clearChunk(c);
									}
								}
							}
						}
					});

				}
			});
		}
	}

	public World getWorld(String id) {
		return worlds.get(id);
	}

	public void chunkUnloaded(World w) {
		if (w.chunks.isEmpty()) {
			worlds.remove(w.id);
		}
	}

	public synchronized WorldSearch getSearchFor(ServerConnection sc, boolean[] states) {
		return getSearchFor(sc, states, false, true);
	}

	public synchronized WorldSearch getSearchIfExists(boolean[] states) {
		synchronized (searches) {
			for (var s : searches) {
				if (Arrays.equals(s.searchTargets, states)) {
					return s;
				}
			}
		}

		return null;
	}

	public synchronized WorldSearch getSearchFor(ServerConnection sc, boolean[] states, boolean active, boolean searchNow) {
		synchronized (searches) {

			for (var s : searches) {
				if (Arrays.equals(s.searchTargets, states)) {
					s.using.add(sc);
					return s;
				}
			}

			WorldSearch search = new WorldSearch(this, states, active);
			search.using.add(sc);
			searches.add(search);
			if (searchNow) search.performInitialSearch();
			search.searchNew = false;
			check = BlockStates.unionBlockSet(check, search.searchTargets);
			search.activate();
			return search;
		}
	}

	public synchronized void stopSearchingFor(ServerConnection sc, boolean[] states) {
		synchronized (searches) {
			for (var s : searches) {
				if (Arrays.equals(s.searchTargets, states)) {
					s.using.remove(sc);
					if (s.using.isEmpty()) {
						s.deactivate();
						searches.remove(s);
						updateSuperSearch();
						return;
					}
				}
			}
		}
	}

	public synchronized void stopSearchingFor(ServerConnection sc, WorldSearch s) {
		synchronized (searches) {

			s.using.remove(sc);
			if (s.using.isEmpty()) {
				s.deactivate();
				searches.remove(s);
				updateSuperSearch();
				return;
			}
		}
	}

	private void updateSuperSearch() {
		check = BlockStates.getBlockSet();
		for (var search : searches) {
			check = BlockStates.unionBlockSet(check, search.searchTargets);
		}
	}

}