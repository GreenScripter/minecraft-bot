package greenscripter.minecraft.play.inventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import greenscripter.minecraft.gameinfo.Registries;
import greenscripter.minecraft.gameinfo.Registries.ItemInfo;
import greenscripter.minecraft.gameinfo.RegistryTags;

public class ItemId {

	public static Map<Integer, String> itemRegistry = Registries.registriesFromIds.get("minecraft:item");
	public static Map<String, Integer> reverseItemRegistry = new HashMap<>();
	public static Map<Integer, ItemInfo> itemInfo = new HashMap<>();
	public static Map<String, Set<Integer>> itemTags = new HashMap<>();
	static {
		for (var id : itemRegistry.entrySet()) {
			itemInfo.put(id.getKey(), Registries.itemInfo.get(id.getValue()));
			reverseItemRegistry.put(id.getValue(), id.getKey());
		}
		for (var tag : RegistryTags.itemTags.entrySet()) {
			Set<Integer> ids = new HashSet<>();
			itemTags.put(tag.getKey(), ids);
			for (String s : tag.getValue()) {
				ids.add(reverseItemRegistry.get(s));
			}
		}
	}

	public static int get(String identifier) {
		return reverseItemRegistry.get(identifier);
	}

	public static String get(int id) {
		return itemRegistry.get(id);
	}

	public static ItemInfo info(int id) {
		return itemInfo.get(id);
	}

	public static ItemInfo info(String id) {
		return Registries.itemInfo.get(id);
	}

	public static Set<String> stringTags(String id) {
		return RegistryTags.getItemTag(id);
	}

	public static Set<Integer> tags(String id) {
		return itemTags.get(id);
	}

}
