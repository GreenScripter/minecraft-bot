package greenscripter.minecraft.atests;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.c2s.play.ClientInfoPacket;
import greenscripter.minecraft.play.data.ClientConfigData;
import greenscripter.minecraft.play.handler.DeathPlayHandler;
import greenscripter.minecraft.play.handler.KeepAlivePlayHandler;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.play.handler.TeleportRequestPlayHandler;
import greenscripter.minecraft.play.handler.WorldPlayHandler;
import greenscripter.minecraft.play.other.SearchPlayHandler;

public class SwarmSearchTest {

	public static void main(String[] args) throws Exception {
		List<ServerConnection> next = new ArrayList<>();
		List<ServerConnection> connections = new ArrayList<>();
		new Thread(() -> {
			long lastLog = System.currentTimeMillis();
			long max = 0;
			long min = Integer.MAX_VALUE;
			int steps = 0;
			long timeSkipped = 0;
			AtomicLong packets = new AtomicLong();
			while (true) {
				long start = System.currentTimeMillis();
				synchronized (next) {
					ClientInfoPacket p = new ClientInfoPacket();
					p.viewDistance = 4;
					next.forEach(sc -> {
						try {
//							if (!sc.name.equals("bot0")) return;
							sc.out.writePacket(p);
							sc.getData(ClientConfigData.class).viewDistance = p.viewDistance;
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
					connections.addAll(0, next);
					next.clear();
				}
				List<ServerConnection> remove = new ArrayList<>();
				//				for (ServerConnection sc : connections) {
				connections.sort(Comparator.comparing(c -> c.id));
				for (ServerConnection sc : connections) {
					try {
						sc.step();

						while (!sc.waiting()) {
							sc.step();
						}
						sc.tick();

					} catch (Exception e) {
						e.printStackTrace();
						remove.add(sc);
					}
					packets.addAndGet(sc.in.packetCounter);
					sc.in.packetCounter = 0;
				}
				connections.removeAll(remove);
				long duration = System.currentTimeMillis() - start;
				max = Math.max(max, duration);
				min = Math.min(min, duration);
				steps++;
				if (System.currentTimeMillis() - lastLog > 1000) {
					System.out.println("Servicing all clients took " + (duration) + " ms. min " + min + " max " + max + " average " + (System.currentTimeMillis() - lastLog - timeSkipped) / steps + " packets " + packets.get());
					packets.set(0);
					lastLog = System.currentTimeMillis();
					max = 0;
					min = Integer.MAX_VALUE;
					steps = 0;
					timeSkipped = 0;
				}
				if (duration < 50) {
					try {
						timeSkipped += 50 - duration;
						Thread.sleep(50 - duration);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		WorldPlayHandler worldHandler = new WorldPlayHandler();
		List<PlayHandler> handler = List.of(//
				new KeepAlivePlayHandler(), //
				new DeathPlayHandler(), //
				worldHandler, //
				new TeleportRequestPlayHandler(),//
				new SearchPlayHandler()//
		);

		int start = args.length == 2 ? Integer.parseInt(args[1]) : 0;
		for (int i = 0 + start; i < 100 + start; i++) {
			int c = i;
			new Thread(() -> {
				try {
					String name = args.length != 0 ? args[0] + c : "bot" + c;
					UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
					ServerConnection sc = new ServerConnection("localhost", 20255, name, uuid, handler);
					sc.id = c;
					//					sc.bungeeMode = true;
					//				connections.add(sc);
					while (true) {
						sc.step();
						if (sc.connectionState.equals(ServerConnection.ConnectionState.PLAY)) {
							synchronized (next) {
								next.add(sc);
							}
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
			Thread.sleep(40);
		}

	}

}
