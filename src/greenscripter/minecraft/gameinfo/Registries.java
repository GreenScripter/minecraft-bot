package greenscripter.minecraft.gameinfo;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Registries {

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

	static {
		long start = System.currentTimeMillis();
		try {
			String registriesString = ResourceExtractor.getJSON("greenscripter/minecraft/resources/reports/registries.json");
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
		safeAttack = new boolean[Registries.registries.get("minecraft:entity_type").values().stream().max(Integer::compareTo).orElse(0)];
		for (int i = 0; i < safeAttack.length; i++) {
			safeAttack[i] = true;
		}
		for (int i = 0; i < kickIfHit.length; i++) {
			safeAttack[Registries.registries.get("minecraft:entity_type").get(kickIfHit[i])] = false;
		}

		System.out.println("Took " + (System.currentTimeMillis() - start) + " ms to load registries.");

	}
}
