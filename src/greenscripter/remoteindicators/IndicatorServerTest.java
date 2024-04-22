package greenscripter.remoteindicators;

import greenscripter.minecraft.utils.Vector;

public class IndicatorServerTest {

	public static void main(String[] args) throws Exception {
		IndicatorServer is = new IndicatorServer(24464);
		int cuboid = is.addCuboid("minecraft:overworld", new Vector(-10, -10, -10), new Vector(10, 80, 10), IndicatorServer.getColor(255, 0, 0, 255));
		while (true) {
			for (int i = 0; i < 100; i++) {
				is.setCuboid(cuboid, "minecraft:overworld", new Vector(-10, -20 + i, -10), new Vector(10, 80, 10), IndicatorServer.getColor(255, 0, 0, 255));
				Thread.sleep(100);
			}
		}
	}
}
