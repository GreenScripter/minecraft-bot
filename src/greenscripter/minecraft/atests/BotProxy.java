package greenscripter.minecraft.atests;

import java.util.List;
import java.util.Scanner;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

import com.google.gson.Gson;

import greenscripter.minecraft.AccountList;
import greenscripter.minecraft.AsyncSwarmController;
import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.nbt.NBTTagString;
import greenscripter.minecraft.packet.c2s.handshake.HandshakePacket;
import greenscripter.minecraft.packet.c2s.play.ClientInfoPacket;
import greenscripter.minecraft.packet.s2c.play.SystemChatPacket;
import greenscripter.minecraft.packet.s2c.status.PingData;
import greenscripter.minecraft.play.handler.PlayHandler;
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

		controller.joinCallback = sc -> {
			sc.sendPacket(new ClientInfoPacket(10));
		};

		controller.namesToUUIDs = accounts::getUUID;
		controller.botNames = accounts::getName;
		//		controller.bungeeMode = true;

		controller.start();
		controller.connect(accounts.size(), 6);

		BiPredicate<ViewerConnection, String> povCommand = (vc, command) -> {
			if (command.startsWith("pov ")) {
				String target = command.substring(4);
				for (ServerConnection move : controller.getAlive()) {
					if (move.name.equalsIgnoreCase(target)) {
						vc.moveLink(move);
						return true;
					}
				}
				SystemChatPacket reply = new SystemChatPacket();
				reply.content = new NBTTagString("§cUnable to view " + target);
				vc.writePacket(reply);
				return true;
			}
			return false;
		};

		Consumer<ViewerConnection> login = vc -> {
			for (ServerConnection move : controller.getAlive()) {
				if (move.name.equalsIgnoreCase(vc.requestedName)) {
					vc.linked = move;
					move.addPlayHandler(vc);
					return;
				}
			}
			for (ServerConnection move : controller.getAlive()) {
				vc.linked = move;
				move.addPlayHandler(vc);
				return;
			}
		};

		Supplier<String> pingResponse = () -> {
			PingData ping = new PingData();
			ping.version = new PingData.Version("Bot Swarm", new HandshakePacket().version);
			ping.players = new PingData.Players(controller.getAlive().size() + controller.getDead().size(), controller.getAlive().size());
			ping.players.sample = new PingData.Sample[Math.min(controller.getAlive().size(), 30)];
			int i = 0;
			for (ServerConnection sc : controller.getAlive()) {
				if (i >= ping.players.sample.length) break;
				ping.players.sample[i] = new PingData.Sample(sc.name, sc.uuid.toString());
				i++;
			}
			ping.description = new PingData.Description("§9Bot Swarm Command and Control");
			return new Gson().toJson(ping);
		};

		new Thread(() -> {
			try (ServerSocket ss = new ServerSocket(25565)) {
				while (true) {
					Socket s = ss.accept();
					try {
						ViewerConnection vc = new ViewerConnection(null, s);
						vc.commandHandlers.add(povCommand);
						vc.loggedInCallback = login;
						vc.pingResponse = pingResponse;
						vc.start();
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
