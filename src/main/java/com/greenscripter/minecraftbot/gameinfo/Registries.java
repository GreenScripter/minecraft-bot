package com.greenscripter.minecraftbot.gameinfo;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.greenscripter.minecraftbot.utils.ResourceExtractor;

public class Registries {

	public static void main(String[] args) {
		System.out.println(registriesFromIds.get("minecraft:item").get(166));
	}

	public static Map<String, Integer> registryIds = new HashMap<>();
	public static Map<Integer, String> idsOfRegistries = new HashMap<>();
	public static Map<String, Map<String, Integer>> registries = new HashMap<>();
	public static Map<String, Map<Integer, String>> registriesFromIds = new HashMap<>();

	public static String[] kickIfHit = new String[] { // These are hardcoded in game using instance of checks.
			"minecraft:item", //
			"minecraft:arrow", //
			"minecraft:experience_orb", //
			"minecraft:spectral_arrow", //
			"minecraft:trident" //
	};
	public static boolean[] safeAttack;

	public static Map<String, ItemInfo> itemInfo = new HashMap<>();

	public static Integer get(String registry, String name) {
		return registries.get(registry).get(name);
	}

	public static String get(String registry, Integer id) {
		return registriesFromIds.get(registry).get(id);
	}

	static {
		long start = System.currentTimeMillis();
		try {
			String registriesString = ResourceExtractor.getResourceString("com/greenscripter/minecraftbot/reports/registries.json");
			Map<String, JsonElement> registries = JsonParser.parseString(registriesString).getAsJsonObject().asMap();
			for (var e : registries.entrySet()) {
				registryIds.put(e.getKey(), e.getValue().getAsJsonObject().get("protocol_id").getAsInt());
				idsOfRegistries.put(e.getValue().getAsJsonObject().get("protocol_id").getAsInt(), e.getKey());
				Map<String, Integer> stoi = new HashMap<>();
				Map<Integer, String> itos = new HashMap<>();
				for (var e2 : e.getValue().getAsJsonObject().get("entries").getAsJsonObject().asMap().entrySet()) {
					stoi.put(e2.getKey(), e2.getValue().getAsJsonObject().get("protocol_id").getAsInt());
					itos.put(e2.getValue().getAsJsonObject().get("protocol_id").getAsInt(), e2.getKey());
				}
				Registries.registries.put(e.getKey(), stoi);
				Registries.registriesFromIds.put(e.getKey(), itos);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		safeAttack = new boolean[Registries.registries.get("minecraft:entity_type").values().stream().max(Integer::compareTo).orElse(0) + 1];
		for (int i = 0; i < safeAttack.length; i++) {
			safeAttack[i] = true;
		}
		for (int i = 0; i < kickIfHit.length; i++) {
			safeAttack[Registries.registries.get("minecraft:entity_type").get(kickIfHit[i])] = false;
		}
		try {
			String registriesString = ResourceExtractor.getResourceString("com/greenscripter/minecraftbot/itemInfo.json");
			Type collectionType = new TypeToken<HashMap<String, ItemInfo>>() {
			}.getType();
			Map<String, ItemInfo> registries = new Gson().fromJson(registriesString, collectionType);
			itemInfo.putAll(registries);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Took " + (System.currentTimeMillis() - start) + " ms to load registries.");

	}

	public static class ItemInfo {

		public boolean nestable;
		public int maxStack;
		public boolean isblock;
		public String blockId;

		public String toString() {
			return "ItemInfo [nestable=" + nestable + ", maxStack=" + maxStack + ", isblock=" + isblock + ", " + (blockId != null ? "blockId=" + blockId : "") + "]";
		}

	}
	/*Extract item information from fabric
		JsonObject allItems = new JsonObject();
		var registry = ClientRegistryLayer.createRegistryAccess().compositeAccess();
		var items = registry.lookup(Registries.ITEM).get();
		var blocks = registry.lookup(Registries.BLOCK).get();
		for (var item : items) {
			Identifier identifier = items.getKey(item);
			JsonObject itemInfo = new JsonObject();
			itemInfo.add("nestable", new JsonPrimitive(item.canFitInsideContainerItems()));
			itemInfo.add("maxStack", new JsonPrimitive(item.getDefaultMaxStackSize()));
			itemInfo.add("isBlock", new JsonPrimitive(item instanceof BlockItem));
	
			if (item instanceof BlockItem block) {
				itemInfo.add("blockId", new JsonPrimitive(blocks.getKey(block.getBlock()) + ""));
			}
			allItems.add(identifier + "", itemInfo);
	
		}
		try {
			FileOutputStream out = new FileOutputStream("itemInfo.json");
			out.write(new GsonBuilder().setPrettyPrinting().create().toJson(allItems).getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	*/
}
