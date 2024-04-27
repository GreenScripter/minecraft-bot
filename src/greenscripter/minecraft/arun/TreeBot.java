package greenscripter.minecraft.arun;

import java.util.ArrayList;
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
		
		worldHandler.worlds.getSearchFor(null, logs, false, true).render = render;

		AsyncSwarmController controller = new AsyncSwarmController("localhost", 20255, handlers);
		controller.joinCallback = sc -> {
			if (sc.id % 10 == 0) sc.sendPacket(new ClientInfoPacket(10));
			sc.setData(TreeBotGlobalData.class, global);
			sc.setData(TreeBotLocalData.class, new TreeBotLocalData());
		};

		long start = System.currentTimeMillis();
		controller.localHandlers = sc -> {
			TreeBotStateMachine state = new TreeBotStateMachine(sc);
			//			state.profiling = true;
			return List.of(new PlayTickHandler(sc2 -> {
				if (System.currentTimeMillis() - start < 3000) return;
				if (sc2.getData(WorldData.class).world == null) return;
				//				long startTick = System.currentTimeMillis();
				state.tick();
				//				if (System.currentTimeMillis() - startTick > 10) {
				//					state.printProfiler();
				//				}

			}));
		};
		controller.start();
		controller.connect(100, 40);

	}

	public static void updateBox(int id, BlockBox box, String dimension, int color) {
		render.setCuboid(id, dimension, new Vector(box.pos1).add(-0.5, 0, -0.5), new Vector(box.pos2).add(0.5, 1, 0.5), color);
	}

	static class TreeBotGlobalData extends PlayData {

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
