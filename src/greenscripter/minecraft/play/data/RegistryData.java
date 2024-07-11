package greenscripter.minecraft.play.data;

import java.util.HashMap;
import java.util.Map;

import greenscripter.minecraft.utils.DynamicRegistry;

public class RegistryData implements PlayData {

	public Map<String, DynamicRegistry> registries = new HashMap<>();
	
	public DynamicRegistry getRegistry(String name) {
		return registries.get(name);
	}

}
