package greenscripter.minecraft.world;

import java.util.HashMap;
import java.util.Map;

public class Worlds {

	public Map<String, World> worlds = new HashMap<>();

	public World getWorld(String id) {
		return worlds.get(id);
	}

	public void chunkUnloaded(World w) {
		if (w.chunks.isEmpty()) {
			worlds.remove(w.id);
		}
	}

}