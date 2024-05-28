package greenscripter.minecraft.arun;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.io.File;
import java.nio.file.Files;

import com.google.gson.Gson;

import greenscripter.minecraft.AccountList;
import greenscripter.minecraft.AsyncSwarmController;
import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.BlockStates;
import greenscripter.minecraft.packet.c2s.play.ClientInfoPacket;
import greenscripter.minecraft.play.data.PlayData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.play.handler.PlayTickHandler;
import greenscripter.minecraft.play.handler.WorldPlayHandler;
import greenscripter.minecraft.play.other.KillAuraHandler;
import greenscripter.minecraft.utils.BlockBox;
import greenscripter.minecraft.utils.Vector;
import greenscripter.minecraft.world.PathFinder;
import greenscripter.minecraft.world.TunnelPathFinder;
import greenscripter.remoteindicators.IndicatorServer;

public class GearBot {

	static boolean[] ironOre = BlockStates.addToBlockSet(BlockStates.getBlockSet(), "minecraft:iron_ore");
	static boolean[] logs = BlockStates.addTagToBlockSet(BlockStates.getBlockSet(), "minecraft:logs");
	static boolean[] leaves = BlockStates.addTagToBlockSet(BlockStates.addTagToBlockSet(BlockStates.getBlockSet(), "minecraft:leaves"), "minecraft:wart_blocks");
	static boolean[] diamonds = BlockStates.addToBlockSet(BlockStates.addToBlockSet(BlockStates.getBlockSet(), "minecraft:diamond_ore"), "minecraft:deepslate_diamond_ore");

	public static IndicatorServer render;

	public static void main(String[] args) throws Exception {
		render = new IndicatorServer(24464);
		AccountList accounts = new Gson().fromJson(Files.readString(new File("accountlist.json").toPath()), AccountList.class);
		System.out.println("Accounts: " + accounts);

		List<PlayHandler> handlers = ServerConnection.getStandardHandlers();
		WorldPlayHandler worldHandler = new WorldPlayHandler();
		handlers.removeIf(p -> p instanceof WorldPlayHandler);
		handlers.add(worldHandler);
		handlers.add(new KillAuraHandler());

		worldHandler.worlds.getSearchFor(null, ironOre, false, true);//.render = render;

		GearBotGlobalData global = new GearBotGlobalData();

		AsyncSwarmController controller = new AsyncSwarmController("localhost", 20255, handlers);
		controller.joinCallback = sc -> {
			if (sc.id % 10 == 0) {
				sc.sendPacket(new ClientInfoPacket(10));
			} else {
				sc.sendPacket(new ClientInfoPacket(2));
			}
			sc.setData(GearBotGlobalData.class, global);
			sc.setData(GearBotLocalData.class, new GearBotLocalData());
		};

		long start = System.currentTimeMillis();
		controller.localHandlers = sc -> {
			GearBotStateMachine machine = new GearBotStateMachine(sc);
			return List.of(new PlayTickHandler(sc2 -> {
				if (System.currentTimeMillis() - start < 5000) return;
				if (sc2.getData(WorldData.class).world == null) return;
				machine.tick();
			}));
		};

		controller.namesToUUIDs = accounts::getUUID;
		controller.botNames = accounts::getName;
//		controller.bungeeMode = true;

		controller.start();
		controller.connect(accounts.size(), 60);

		Scanner scanner = new Scanner(System.in);
		while (true) {
			String line = scanner.nextLine();
			handle(controller, line);
		}

	}

	public static void handle(AsyncSwarmController controller, String line) {
		controller.reconnectDead(controller.takeDead(controller.getDead()), 6000);
	}

	public static void updateBox(int id, BlockBox box, String dimension, int color) {
		render.setCuboid(id, dimension, new Vector(box.pos1).add(-0.5, 0, -0.5), new Vector(box.pos2).add(0.5, 1, 0.5), color);
	}

	static class GearBotGlobalData implements PlayData {

		ExecutorService pathfinding = Executors.newFixedThreadPool(4);
	}

	static class GearBotLocalData implements PlayData {

		PathFinder pathfinder = new PathFinder();
		TunnelPathFinder tunneler = new TunnelPathFinder();
		{
			pathfinder.infiniteVClipAllowed = false;
			pathfinder.timeout = 200;

			tunneler.infiniteVClipAllowed = false;
			tunneler.timeout = 400;
		}

	}
}
