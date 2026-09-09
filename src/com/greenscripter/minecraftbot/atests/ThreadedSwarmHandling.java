package com.greenscripter.minecraftbot.atests;

import java.util.List;
import java.util.UUID;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.greenscripter.minecraftbot.ServerConnection;
import com.greenscripter.minecraftbot.play.handler.KeepAlivePlayHandler;
import com.greenscripter.minecraftbot.play.handler.PlayHandler;
import com.greenscripter.minecraftbot.play.handler.TeleportRequestPlayHandler;
import com.greenscripter.minecraftbot.play.other.CirclePlayHandler;

public class ThreadedSwarmHandling {

	public static void main(String[] args) throws Exception {

		List<PlayHandler> handler = List.of(//
				new KeepAlivePlayHandler(), //
				new TeleportRequestPlayHandler(), //
				new CirclePlayHandler());

		for (int i = 0; i < 100; i++) {
			int c = i;
			new Thread(() -> {
				try {
					String name = "bot" + c;
					UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
					ServerConnection sc = new ServerConnection("localhost", 20255, name, uuid, handler);
					sc.connect();
					sc.blocking = true;
					boolean started = false;
					//					sc.bungeeMode = true;
					//				connections.add(sc);
					while (true) {
						sc.step();
						if (!started && sc.connectionState.equals(ServerConnection.ConnectionState.PLAY)) {
							started = true;
							new Thread(() -> {
								try {
									while (true) {
										long start = System.currentTimeMillis();
										sc.tick();
										long duration = System.currentTimeMillis() - start;
										if (duration < 50) {
											try {
												Thread.sleep(50 - duration);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
										}
									}
								} catch (IOException e) {
									e.printStackTrace();
								}

							}).start();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
			Thread.sleep(10);
		}

	}

}
