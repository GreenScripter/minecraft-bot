package greenscripter.minecraft.gameinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class RegistryTags {

	public static void main(String[] args) throws InterruptedException {
		System.out.println(blockTags.keySet());
		Registries.registries.getClass();
		BlockStates.blockStates.getClass();
//		System.out.println(BlockStates.blockStates.keySet());
		Thread.sleep(20000);
	}

	public static Map<String, Set<String>> blockTags = new HashMap<>();
	public static Map<String, Set<String>> itemTags = new HashMap<>();

	public static Set<String> getBlockTag(String id) {
		return blockTags.get(id);
	}

	public static Set<String> getItemTag(String id) {
		return itemTags.get(id);
	}

	public static boolean matchesBlockTag(String id, String block) {
		Set<String> tag = blockTags.get(id);
		if (tag == null) return false;
		return tag.contains(block);
	}

	public static boolean matchesItemTag(String id, String item) {
		Set<String> tag = itemTags.get(id);
		if (tag == null) return false;
		return tag.contains(item);
	}

	static {
		long start = System.currentTimeMillis();
		try {
			Map<String, String> itemTagJsons = ResourceExtractor.getJSONs("greenscripter/minecraft/resources/data/minecraft/tags/items");
			for (var e : itemTagJsons.entrySet()) {
				Set<String> parts = new HashSet<>();
				JsonElement element = JsonParser.parseString(e.getValue());
				if (element.isJsonObject()) {
					JsonElement values = element.getAsJsonObject().get("values");
					if (values != null && values.isJsonArray()) {
						JsonArray entries = values.getAsJsonArray();
						for (JsonElement el : entries) {
							parts.add(el.getAsString());
						}
					}
				}
				itemTags.put("minecraft:" + e.getKey().replace(".json", ""), parts);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Map<String, String> blockTagJsons = ResourceExtractor.getJSONs("greenscripter/minecraft/resources/data/minecraft/tags/blocks");
			for (var e : blockTagJsons.entrySet()) {
				Set<String> parts = new HashSet<>();
				JsonElement element = JsonParser.parseString(e.getValue());
				if (element.isJsonObject()) {
					JsonElement values = element.getAsJsonObject().get("values");
					if (values != null && values.isJsonArray()) {
						JsonArray entries = values.getAsJsonArray();
						for (JsonElement el : entries) {
							parts.add(el.getAsString());
						}
					}
				}
				blockTags.put("minecraft:" + e.getKey().replace(".json", ""), parts);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean removed = true;
		while (removed) {
			removed = false;
			for (Set<String> tag : itemTags.values()) {
				for (String s : new ArrayList<>(tag)) {
					if (s.startsWith("#")) {
						removed = true;
						tag.remove(s);
						tag.addAll(itemTags.get(s.substring(1)));
					}
				}
			}
		}
		removed = true;
		while (removed) {
			removed = false;
			for (Set<String> tag : blockTags.values()) {
				for (String s : new ArrayList<>(tag)) {
					if (s.startsWith("#")) {
						removed = true;
						tag.remove(s);
						tag.addAll(blockTags.get(s.substring(1)));
					}
				}
			}
		}
		System.out.println("Took " + (System.currentTimeMillis() - start) + " ms to load tags.");
	}
}
