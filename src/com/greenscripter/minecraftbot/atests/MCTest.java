package com.greenscripter.minecraftbot.atests;

import java.util.List;

import com.greenscripter.minecraftbot.AsyncSwarmController;
import com.greenscripter.minecraftbot.packet.c2s.play.ClientInfoPacket;
import com.greenscripter.minecraftbot.play.data.PositionData;
import com.greenscripter.minecraftbot.play.data.WorldData;
import com.greenscripter.minecraftbot.play.handler.DeathPlayHandler;
import com.greenscripter.minecraftbot.play.handler.EntityPlayHandler;
import com.greenscripter.minecraftbot.play.handler.InventoryPlayHandler;
import com.greenscripter.minecraftbot.play.handler.KeepAlivePlayHandler;
import com.greenscripter.minecraftbot.play.handler.PlayHandler;
import com.greenscripter.minecraftbot.play.handler.PlayerPlayHandler;
import com.greenscripter.minecraftbot.play.handler.TeleportRequestPlayHandler;
import com.greenscripter.minecraftbot.play.handler.WorldPlayHandler;
import com.greenscripter.minecraftbot.utils.Position;

public class MCTest {

	public static void main(String[] args) throws Exception {
		List<PlayHandler> handlers = List.of(//
				new KeepAlivePlayHandler(), //
				new DeathPlayHandler(), //
				new WorldPlayHandler(), //
				new TeleportRequestPlayHandler(),//
				new EntityPlayHandler(),//
				new PlayerPlayHandler(),//
				new InventoryPlayHandler()//
		);

		AsyncSwarmController controller = new AsyncSwarmController("localhost", 20255, handlers);
		controller.joinCallback = sc -> {
			sc.sendPacket(new ClientInfoPacket(10));
		};
		controller.start();
		controller.connect(1, 40);
		Thread.sleep(10000);
		controller.getAlive().forEach(sc -> {
			System.out.println("callback 1");
			WorldData data = sc.getData(WorldData.class);
			PositionData dataPos = sc.getData(PositionData.class);
			data.useItemOn(sc, 0, new Position(dataPos.pos).add(0, 2, 0), 0);
		});
		Thread.sleep(1000);
		controller.getAlive().forEach(sc -> {
			System.out.println("callback 1");
			WorldData data = sc.getData(WorldData.class);
			PositionData dataPos = sc.getData(PositionData.class);
			data.useItemOn(sc, 0, new Position(dataPos.pos).add(0, 2, 0), 0);
		});
		//		controller.getAlive().forEach(sc -> {
		//			System.out.println("callback 2");
		//			InventoryData data = sc.getData(InventoryData.class);
		//			//			data.swapSlots(sc, data.inv.getInventorySlot(0), 40);
		//		});

	}

}
