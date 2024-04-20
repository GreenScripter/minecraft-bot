package greenscripter.minecraft.atests;

import java.util.List;

import greenscripter.minecraft.AsyncSwarmController;
import greenscripter.minecraft.packet.c2s.play.ClientInfoPacket;
import greenscripter.minecraft.play.data.InventoryData;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.handler.DeathPlayHandler;
import greenscripter.minecraft.play.handler.EntityPlayHandler;
import greenscripter.minecraft.play.handler.InventoryPlayHandler;
import greenscripter.minecraft.play.handler.KeepAlivePlayHandler;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.play.handler.PlayerPlayHandler;
import greenscripter.minecraft.play.handler.TeleportRequestPlayHandler;
import greenscripter.minecraft.play.handler.WorldPlayHandler;
import greenscripter.minecraft.utils.Position;

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
