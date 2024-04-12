package greenscripter.minecraft.gameinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BlockStates {

	public static Map<String, Map<String, List<String>>> blockProperties = new HashMap<>();
	public static Map<String, BlockState> defaultStates = new HashMap<>();
	public static Map<String, List<BlockState>> blockStates = new HashMap<>();
	public static Map<Integer, BlockState> idsToStates = new HashMap<>();

	public static Map<String, List<String>> getBlockProperties(String block) {
		return blockProperties.get(block);
	}

	public static List<String> getBlockPropertyStates(String block, String property) {
		return blockProperties.get(block).get(property);
	}

	public static BlockState getDefaultBlockState(String block) {
		return defaultStates.get(block);
	}

	public static List<BlockState> getBlockStates(String block) {
		return blockStates.get(block);
	}

	public static BlockState getState(int id) {
		return idsToStates.get(id);
	}

	static {
		long start = System.currentTimeMillis();
		try {
			String registriesString = ResourceExtractor.getJSON("greenscripter/minecraft/resources/reports/blocks.json");
			Map<String, JsonElement> registries = JsonParser.parseString(registriesString).getAsJsonObject().asMap();
			for (var e : registries.entrySet()) {
				String block = e.getKey();

				Map<String, List<String>> properties = new HashMap<>();
				if (e.getValue().getAsJsonObject().get("properties") != null) {
					JsonObject blockProperties = e.getValue().getAsJsonObject().get("properties").getAsJsonObject();

					for (var e2 : blockProperties.entrySet()) {
						String name = e2.getKey();
						List<String> states = new ArrayList<>(e2.getValue().getAsJsonArray().size());
						for (var s : e2.getValue().getAsJsonArray()) {
							states.add(s.getAsString());
						}
						properties.put(name, states);
					}
				}
				BlockStates.blockProperties.put(block, properties);

				List<BlockState> states = new ArrayList<>(e.getValue().getAsJsonObject().get("states").getAsJsonArray().size());
				for (var s : e.getValue().getAsJsonObject().get("states").getAsJsonArray()) {
					var state = s.getAsJsonObject();

					BlockState blockState = new BlockState(state.get("id").getAsInt(), block, new HashMap<>(), state.get("default") != null);
					if (state.get("proporties") != null) for (var p : state.get("proporties").getAsJsonObject().entrySet()) {
						blockState.properties.put(p.getKey(), p.getValue().getAsString());
					}
					if (blockState.isDefault) {
						defaultStates.put(block, blockState);
					}
					idsToStates.put(blockState.id, blockState);
				}
				blockStates.put(block, states);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Took " + (System.currentTimeMillis() - start) + " ms to load block states.");

	}

	public static record BlockState(int id, String block, Map<String, String> properties, boolean isDefault) {

	}
}
