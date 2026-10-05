package com.greenscripter.minecraftbot.test;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.greenscripter.minecraftbot.AsyncSwarmController;
import com.greenscripter.minecraftbot.ServerConnection;
import com.greenscripter.minecraftbot.packet.c2s.play.ChatMessagePacket;
import com.greenscripter.minecraftbot.test.utils.TestOnServerBase;

public class JoinGameTest extends TestOnServerBase {

	@Test
	public void joinGameTest() throws Exception {
		var controller = new AsyncSwarmController(this.server.getHost(), this.server.getPort(), ServerConnection.getStandardHandlers());
		try {
			controller.start();
			controller.connect(1, 0);

			server.waitForOutputContains(5000, "bot0 joined the game");
		} finally {
			controller.shutdown();
		}
	}

	@Test
	public void joinGame100Test() throws Exception {
		var controller = new AsyncSwarmController(this.server.getHost(), this.server.getPort(), ServerConnection.getStandardHandlers());
		try {
			controller.start();
			controller.connect(100, 0);

			server.waitForOutputContains(5000, IntStream.range(0, 100).mapToObj(i -> "bot" + i + " joined the game").toList());
		} finally {
			controller.shutdown();
		}
	}

	@Test
	public void joinGameChatTest() throws Exception {
		var controller = new AsyncSwarmController(this.server.getHost(), this.server.getPort(), ServerConnection.getStandardHandlers());
		try {
			controller.start();
			controller.joinCallback = bot -> {
				bot.sendPacket(new ChatMessagePacket("Hello World"));
			};
			controller.connect(10, 0);

			server.waitForOutputContains(5000, IntStream.range(0, 10).mapToObj(i -> "<bot" + i + "> Hello World").toList());
		} finally {
			controller.shutdown();
		}
	}

	@Test
	public void leaveGameTest() throws Exception {
		var controller = new AsyncSwarmController(this.server.getHost(), this.server.getPort(), ServerConnection.getStandardHandlers());
		try {
			controller.start();
			controller.connect(10, 0);
			server.waitForOutputContains(5000, IntStream.range(0, 10).mapToObj(i -> "bot" + i + " joined the game").toList());

			waitFor(1000, () -> controller.getAlive().size() == 10);
			controller.shutdown();
			server.waitForOutputContains(5000, IntStream.range(0, 10).mapToObj(i -> "bot" + i + " left the game").toList());
		} finally {
			controller.shutdown();
		}
	}

}
