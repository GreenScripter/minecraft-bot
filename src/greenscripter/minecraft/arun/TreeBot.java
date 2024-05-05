package greenscripter.minecraft.arun;

import java.util.List;
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
import greenscripter.minecraft.utils.Vector;
import greenscripter.minecraft.world.PathFinder;
import greenscripter.remoteindicators.IndicatorServer;

public class TreeBot {

	static boolean[] logs = BlockStates.addTagToBlockSet(BlockStates.getBlockSet(), "minecraft:logs");
	static boolean[] leaves = BlockStates.addTagToBlockSet(BlockStates.addTagToBlockSet(BlockStates.getBlockSet(), "minecraft:leaves"), "minecraft:wart_blocks");

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

	static class TreeBotGlobalData implements PlayData {

		ExecutorService pathfinding = Executors.newFixedThreadPool(4);
	}

	static class TreeBotLocalData implements PlayData {

		PathFinder pathfinder = new PathFinder();
		{
			pathfinder.infiniteVClipAllowed = false;
			pathfinder.timeout = 200;
		}

	}

}
