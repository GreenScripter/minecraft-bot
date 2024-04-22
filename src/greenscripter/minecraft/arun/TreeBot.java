package greenscripter.minecraft.arun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import greenscripter.minecraft.AsyncSwarmController;
import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.BlockStates;
import greenscripter.minecraft.packet.c2s.play.ClientInfoPacket;
import greenscripter.minecraft.play.data.PlayData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.play.handler.PlayTickHandler;
import greenscripter.minecraft.play.handler.WorldPlayHandler;
import greenscripter.minecraft.utils.BlockBox;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.utils.Vector;
import greenscripter.minecraft.world.Chunk;
import greenscripter.minecraft.world.PathFinder;
import greenscripter.minecraft.world.World;
import greenscripter.remoteindicators.IndicatorServer;

public class TreeBot {

	static boolean[] logs = BlockStates.addTagToBlockSet(BlockStates.getBlockSet(), "minecraft:logs");
	static boolean[] leaves = BlockStates.addTagToBlockSet(BlockStates.getBlockSet(), "minecraft:leaves");

	public static IndicatorServer render;

	public static void main(String[] args) throws Exception {
		render = new IndicatorServer(24464);

		List<PlayHandler> handlers = ServerConnection.getStandardHandlers();
		WorldPlayHandler worldHandler = new WorldPlayHandler();
		handlers.removeIf(p -> p instanceof WorldPlayHandler);
		handlers.add(worldHandler);

		TreeBotGlobalData global = new TreeBotGlobalData();

		worldHandler.chunkLoadListeners.add((sc, c) -> {
			boolean[][] checkMask = new boolean[16][16];
			for (int y = c.height - 2; y >= 0; y--) {
				for (int z = 0; z < 16; z++) {
					for (int x = 0; x < 16; x++) {
						if (checkMask[x][z]) {
							continue;
						}
						int block = c.getBlockInChunk(x, y, z);
						if (logs[block] && leaves[c.getBlockInChunk(x, y + 1, z)]) {
							checkMask[x][z] = true;
							Tree tree = new Tree(global.searched, c.world, new Position(x + c.chunkX * 16, y + c.min_y, z + c.chunkZ * 16));
							tree.chunk = Chunk.mergeCoords(c.chunkX, c.chunkZ);
							tree.dimension = c.world.id;
							if (!tree.blocks.isEmpty()) {
								global.trees.add(tree);
								tree.renderId = render.addCuboid(c.world.id, new Vector(tree.boundingBox.pos1).add(-0.5, 0, -0.5), new Vector(tree.boundingBox.pos2.copy()).add(0.5, 1, 0.5), IndicatorServer.getColor(0, 255, 255, 255));
							}
						}
					}
				}
			}
		});

		worldHandler.chunkUnloadListeners.add((sc, c) -> {
			long chunk = Chunk.mergeCoords(c.chunkX, c.chunkZ);

			global.trees.removeIf(t -> {
				if (t.chunk == chunk) {
					t.blocks.forEach(p -> global.searched.remove(p.getEncoded()));
					render.removeShape(t.renderId);
					return true;
				}
				return false;
			});
		});

		AsyncSwarmController controller = new AsyncSwarmController("localhost", 20255, handlers);
		controller.joinCallback = sc -> {
			if (sc.id % 10 == 0) sc.sendPacket(new ClientInfoPacket(10));
			sc.setData(TreeBotGlobalData.class, global);
			sc.setData(TreeBotLocalData.class, new TreeBotLocalData());
		};

		controller.localHandlers = sc -> {
			TreeBotStateMachine state = new TreeBotStateMachine(sc);
			return List.of(new PlayTickHandler(sc2 -> {
				if (sc2.getData(WorldData.class).world == null) return;
				state.tick();
			}));
		};
		controller.start();
		controller.connect(100, 40);

	}

	public static void updateBox(int id, BlockBox box, String dimension, int color) {
		render.setCuboid(id, dimension, new Vector(box.pos1).add(-0.5, 0, -0.5), new Vector(box.pos2).add(0.5, 1, 0.5), color);
	}

	static class TreeBotGlobalData extends PlayData {

		List<Tree> trees = new ArrayList<>();
		Set<Long> searched = new HashSet<>();
		ExecutorService pathfinding = Executors.newFixedThreadPool(4);
	}

	static class TreeBotLocalData extends PlayData {

		PathFinder pathfinder = new PathFinder();
		{
			pathfinder.infiniteVClipAllowed = false;
			pathfinder.timeout = 200;
		}

	}

	static class Tree {

		long chunk;
		List<Position> blocks = new ArrayList<>();
		BlockBox boundingBox;
		int renderId;
		String dimension;

		public Tree(Set<Long> found, World w, Position start) {
			List<Position> queue = new ArrayList<>();
			queue.add(start.copy());
			while (!queue.isEmpty()) {
				Position next = queue.remove(queue.size() - 1);
				int block = w.getBlock(next);
				if (block >= 0 && logs[block]) {
					if (found.add(next.getEncoded())) {
						blocks.add(next);
						for (int x = -1; x <= 1; x++) {
							for (int y = -1; y <= 1; y++) {
								for (int z = -1; z <= 1; z++) {
									if (x == y && y == z && x == 0) continue;
									queue.add(next.copy().add(x, y, z));
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
