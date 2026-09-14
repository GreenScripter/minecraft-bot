package com.greenscripter.minecraftbot.test.utils;

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
		if (!server.waitForStart(30000)) {
			server.close();
			throw new Exception("Failed to detect server start.");
		}
	}

}
