package greenscripter.minecraft.atests;

import java.util.List;

import greenscripter.minecraft.AsyncSwarmController;
import greenscripter.minecraft.packet.c2s.play.ClientInfoPacket;
import greenscripter.minecraft.play.handler.DeathPlayHandler;
import greenscripter.minecraft.play.handler.EntityPlayHandler;
import greenscripter.minecraft.play.handler.InventoryPlayHandler;
import greenscripter.minecraft.play.handler.KeepAlivePlayHandler;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.play.handler.PlayerPlayHandler;
import greenscripter.minecraft.play.handler.TeleportRequestPlayHandler;
import greenscripter.minecraft.play.handler.WorldPlayHandler;

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

	}

}
