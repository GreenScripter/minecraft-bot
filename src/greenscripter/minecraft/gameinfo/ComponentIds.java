package greenscripter.minecraft.gameinfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import greenscripter.minecraft.play.inventory.components.Component;
import greenscripter.minecraft.play.inventory.components.Components;

public class ComponentIds {

	public static Integer get(String name) {
		return Registries.get("minecraft:data_component_type", name);
	}

	public static String get(Integer id) {
		return Registries.get("minecraft:data_component_type", id);
	}
	
	public static Map<Integer, Components> defaultComponents = new HashMap<>();

	public static Map<Integer, Supplier<Component>> componentTypes = new HashMap<>();

	static {

	}
}
