package com.greenscripter.minecraftbot.play.data;

import java.util.HashMap;
import java.util.Map;

import com.greenscripter.minecraftbot.utils.play.DynamicRegistry;

public class RegistryData implements PlayData {

	public Map<String, DynamicRegistry> registries = new HashMap<>();

	public DynamicRegistry getRegistry(String name) {
		return registries.get(name);
	}

}
