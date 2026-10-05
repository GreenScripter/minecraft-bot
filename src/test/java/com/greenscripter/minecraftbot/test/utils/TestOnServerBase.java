package com.greenscripter.minecraftbot.test.utils;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "SERVER_TESTS", matches = "true")
@TestInstance(Lifecycle.PER_CLASS)
public class TestOnServerBase {

	@AutoClose
	public Server server;

	@BeforeAll
	public void init() throws Exception {
		server = ServerGenerator.getServer();
		server.open();
		server.print();
		if (!server.waitForStart(30000)) {
			server.close();
			throw new Exception("Failed to detect server start.");
		}
	}

	public void waitFor(int wait, BooleanSupplier s) {
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < wait) {
			var v = s.getAsBoolean();
			if (v) return;
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException("Waited more than " + wait + " ms.");
	}

	public <T> T waitFor(int wait, Supplier<T> s) {
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < wait) {
			var v = s.get();
			if (v != null) return v;
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException("Waited more than " + wait + " ms.");
	}
}
