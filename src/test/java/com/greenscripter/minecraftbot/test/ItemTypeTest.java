package com.greenscripter.minecraftbot.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.greenscripter.minecraftbot.AsyncSwarmController;
import com.greenscripter.minecraftbot.ServerConnection;
import com.greenscripter.minecraftbot.packet.s2c.play.DisguisedChatPacket;
import com.greenscripter.minecraftbot.play.data.InventoryData;
import com.greenscripter.minecraftbot.play.handler.PlayPacketHandler;
import com.greenscripter.minecraftbot.play.inventory.ItemId;
import com.greenscripter.minecraftbot.play.inventory.ItemUtils;
import com.greenscripter.minecraftbot.test.utils.MarkerPlayerData;
import com.greenscripter.minecraftbot.test.utils.TestOnServerBase;

public class ItemTypeTest extends TestOnServerBase {

	@Test
	public void giveAllTest() throws Exception {
		var controller = new AsyncSwarmController(this.server.getHost(), this.server.getPort(), ServerConnection.getStandardHandlers());
		try {
			controller.start();
			controller.joinCallback = bot -> {
				bot.setData(MarkerPlayerData.class, new MarkerPlayerData());
				bot.addPlayHandler(new PlayPacketHandler(List.of(DisguisedChatPacket.packetId), (up, sc) -> {
					bot.getData(MarkerPlayerData.class).marked = true;
				}));
			};
			controller.connect(100, 0);

			waitFor(5000, () -> controller.getAlive().size() == 100);
			server.waitForOutputContains(5000, IntStream.range(0, 100).mapToObj(i -> "bot" + i + " joined the game").toList());
			var bots = controller.getAlive();
			var allItems = new ArrayList<>(ItemId.itemRegistry.entrySet());
			for (int i = 0; i < allItems.size();) {
				List<Predicate<String>> gives = new ArrayList<>();
				Map<ServerConnection, Integer> expectedItems = new HashMap<>();
				for (var bot : bots) {
					if (i < allItems.size()) {
						var item = allItems.get(i);
						server.sendCommand("give " + bot.name + " " + item.getValue());
						gives.add(s -> s.contains("Gave 1 ") && s.contains(" to " + bot.name));
						expectedItems.put(bot, item.getKey());
						bot.getData(MarkerPlayerData.class).marked = false;
					}
					i++;
				}
				server.sendCommand("say Round Complete");
				server.waitForOutputMatchesAll(1000, gives);
				for (var expect : expectedItems.entrySet()) {
					waitFor(1000, () -> expect.getKey().getData(MarkerPlayerData.class).marked);
					var inv = expect.getKey().getData(InventoryData.class);
					if (expect.getValue() == 0) {
						assertEquals(inv.inv.slots.length, ItemUtils.countEmptySlots(inv.inv.getIterator()), ItemId.get(expect.getValue()));
					} else {
						assertEquals(1, ItemUtils.countSlotsWithItem(expect.getValue(), inv.inv.getIterator()), ItemId.get(expect.getValue()));
					}
				}

				server.sendCommand("clear @a");
			}
		} finally {
			controller.shutdown();
		}
	}

}
