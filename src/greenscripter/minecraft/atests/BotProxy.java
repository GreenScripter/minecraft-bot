package greenscripter.minecraft.atests;

import java.util.List;
import java.util.Scanner;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

import com.google.gson.Gson;

import greenscripter.minecraft.AccountList;
import greenscripter.minecraft.AsyncSwarmController;
import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.c2s.play.ClientInfoPacket;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.play.handler.PlayTickHandler;
import greenscripter.minecraft.play.handler.ViewerConnection;
import greenscripter.minecraft.play.handler.ViewerTrackPlayHandler;
import greenscripter.minecraft.play.handler.WorldPlayHandler;

public class BotProxy {

	public static void main(String[] args) throws Exception {
		AccountList accounts = new Gson().fromJson(Files.readString(new File("accountlist.json").toPath()), AccountList.class);
		System.out.println("Accounts: " + accounts);

		List<PlayHandler> handlers = ServerConnection.getStandardHandlers();
		WorldPlayHandler worldHandler = new WorldPlayHandler();
		handlers.removeIf(p -> p instanceof WorldPlayHandler);
		handlers.add(worldHandler);
		ViewerTrackPlayHandler viewers = new ViewerTrackPlayHandler();
		handlers.add(viewers);

		AsyncSwarmController controller = new AsyncSwarmController("localhost", 20255, handlers);
		viewers.controller = controller;

		controller.joinCallback = sc -> {
			sc.sendPacket(new ClientInfoPacket(10));
		};

		long start = System.currentTimeMillis();
		controller.localHandlers = sc -> {
			return List.of(new PlayTickHandler(sc2 -> {
				if (System.currentTimeMillis() - start < 5000) return;
				if (sc2.getData(WorldData.class).world == null) return;
			}));
		};

		controller.namesToUUIDs = accounts::getUUID;
		controller.botNames = accounts::getName;
		//		controller.bungeeMode = true;

		controller.start();
		controller.connect(accounts.size(), 6);

		new Thread(() -> {
			try (ServerSocket ss = new ServerSocket(25565)) {
				while (true) {
					Socket s = ss.accept();
					ServerConnection sc = controller.getAlive().get(0);
					try {
						sc.addPlayHandler(new ViewerConnection(sc, s));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				String line = scanner.nextLine();
				handle(controller, line);
			}
		}

	}

	public static void handle(AsyncSwarmController controller, String line) {
		controller.reconnectDead(controller.takeDead(controller.getDead()), 6000);
	}

}
