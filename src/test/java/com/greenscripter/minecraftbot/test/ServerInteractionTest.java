package com.greenscripter.minecraftbot.test;

import org.junit.jupiter.api.Test;

import com.greenscripter.minecraftbot.test.utils.TestOnServerBase;

public class ServerInteractionTest extends TestOnServerBase {

	@Test
	public void sayTest() throws Exception {
		server.sendCommand("say Hello");
		server.waitForOutputContains(1000, "[Server] Hello");
	}

	@Test
	public void fillTest() throws Exception {
		server.sendCommand("fill 0 0 0 10 10 10 diamond_block");
		server.waitForOutputContains(1000, "Successfully filled 1331 block(s)");
	}
	
	@Test
	public void summonTest() throws Exception {
		server.sendCommand("summon cow 0 200 0");
		server.waitForOutputContains(1000, "Summoned new Cow");
	}
}
