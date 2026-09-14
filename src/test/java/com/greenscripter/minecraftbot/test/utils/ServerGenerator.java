package com.greenscripter.minecraftbot.test.utils;

import java.util.concurrent.atomic.AtomicInteger;

import java.io.File;

public class ServerGenerator {

	private static AtomicInteger serverid = new AtomicInteger(35565);

	public static boolean serverExists() {
		return new File("testing/template/start.sh").exists();
	}

	public static Server getServer() {
		if (!serverExists()) {
			throw new RuntimeException("No server setup");
		}
		int id = serverid.getAndIncrement();
		return new Server("temp" + id, id);
	}
}
