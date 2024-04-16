package greenscripter.minecraft.atests;

import java.util.List;

import greenscripter.minecraft.AsyncSwarmController;
import greenscripter.minecraft.play.handler.DeathPlayHandler;
import greenscripter.minecraft.play.handler.EntityPlayHandler;
import greenscripter.minecraft.play.handler.KeepAlivePlayHandler;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.play.handler.TeleportRequestPlayHandler;
import greenscripter.minecraft.play.handler.WorldPlayHandler;
import greenscripter.minecraft.play.other.SearchPlayHandler;

public class MCTest {

	public static void main(String[] args) throws Exception {
		List<PlayHandler> handlers = List.of(//
				new KeepAlivePlayHandler(), //
				new DeathPlayHandler(), //
				new WorldPlayHandler(), //
				new TeleportRequestPlayHandler(),//
				new SearchPlayHandler(),//
				new EntityPlayHandler()//
		);

		AsyncSwarmController controller = new AsyncSwarmController("localhost", 20255, handlers);
		controller.start();
		controller.connect(100, 40);

	}

}
