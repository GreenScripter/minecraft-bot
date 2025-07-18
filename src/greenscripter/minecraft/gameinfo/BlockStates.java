package greenscripter.minecraft.gameinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BlockStates {

	public static void main(String[] args) {
		System.out.println(RegistryTags.blockTags);
		System.out.println(blockStates.keySet());
	}

	public static Map<String, Map<String, List<String>>> blockProperties = new HashMap<>();
	public static Map<String, BlockState> defaultStates = new HashMap<>();
	public static Map<String, List<BlockState>> blockStates = new HashMap<>();
	public static Map<Integer, BlockState> idsToStates = new HashMap<>();
	public static boolean[] noCollideIds;

	public static boolean missingOrInSet(int it, boolean[] set) {
		if (it < 0) return true;
		return set[it];
	}

	public static boolean presentInSet(int it, boolean[] set) {
		if (it < 0) return false;
		return set[it];
	}

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

	public static boolean[] getBlockSet() {
		return new boolean[noCollideIds.length];
	}

	public static boolean[] getBlockSetOf(String... blockId) {
		boolean[] set = getBlockSet();
		for (String id : blockId) {
			addToBlockSet(set, id);
		}
		return set;
	}

	public static boolean[] getBlockSetOfTags(String... blockId) {
		boolean[] set = getBlockSet();
		for (String id : blockId) {
			addTagToBlockSet(set, id);
		}
		return set;
	}

	public static boolean[] addToBlockSet(boolean[] set, String blockId) {
		//		System.out.println(blockStates.get(blockId));
		for (BlockState s : blockStates.get(blockId)) {
			set[s.id] = true;
		}
		return set;
	}

	public static boolean[] addTagToBlockSet(boolean[] set, String blockTag) {
		for (String blockId : RegistryTags.getBlockTag(blockTag)) {
			for (BlockState s : blockStates.get(blockId)) {
				set[s.id] = true;
			}
		}
		return set;
	}

	public static boolean[] removeTagFromBlockSet(boolean[] set, String blockTag) {
		for (String blockId : RegistryTags.getBlockTag(blockTag)) {
			for (BlockState s : blockStates.get(blockId)) {
				set[s.id] = false;
			}
		}
		return set;
	}

	public static boolean[] removeFromBlockSet(boolean[] set, String blockId) {
		for (BlockState s : blockStates.get(blockId)) {
			set[s.id] = false;
		}
		return set;
	}

	public static boolean[] unionBlockSet(boolean[] set, boolean[] other) {
		boolean[] union = getBlockSet();
		for (int i = 0; i < union.length; i++) {
			union[i] = set[i] || other[i];
		}
		return union;
	}

	public static boolean[] intersectBlockSet(boolean[] set, boolean[] other) {
		boolean[] intersection = getBlockSet();
		for (int i = 0; i < intersection.length; i++) {
			intersection[i] = set[i] && other[i];
		}
		return intersection;
	}

	public static boolean[] subtractBlockSet(boolean[] set, boolean[] other) {
		boolean[] subtract = getBlockSet();
		for (int i = 0; i < subtract.length; i++) {
			subtract[i] = set[i] && !other[i];
		}
		return subtract;
	}

	public static boolean[] copyBlockSet(boolean[] set) {
		boolean[] copy = getBlockSet();
		for (int i = 0; i < copy.length; i++) {
			copy[i] = set[i];
		}
		return copy;
	}

	static {
		long start = System.currentTimeMillis();
		try {
			String collidesString = ResourceExtractor.getJSON("greenscripter/minecraft/resources/collides.json");
			Map<String, JsonElement> collides = JsonParser.parseString(collidesString).getAsJsonObject().asMap();
			Set<Integer> noCollide = new HashSet<>(collides.keySet().stream().map(i -> Integer.parseInt(i)).toList());

			String registriesString = ResourceExtractor.getJSON("greenscripter/minecraft/resources/reports/blocks.json");
			Map<String, JsonElement> registries = JsonParser.parseString(registriesString).getAsJsonObject().asMap();
			for (var e : registries.entrySet()) {
				String block = e.getKey();
				Definition def = null;

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

				if (e.getValue().getAsJsonObject().get("definition") != null) {
					JsonObject definition = e.getValue().getAsJsonObject().get("definition").getAsJsonObject();
					String type = definition.has("type") ? definition.get("type").getAsString() : null;
					String block_set_type = definition.has("block_set_type") ? definition.get("block_set_type").getAsString() : null;
					String wood_type = definition.has("wood_type") ? definition.get("wood_type").getAsString() : null;
					String color = definition.has("color") ? definition.get("color").getAsString() : null;
					String tree = definition.has("tree") ? definition.get("tree").getAsString() : null;
					String weathering_state = definition.has("weathering_state") ? definition.get("weathering_state").getAsString() : null;
					String base_state = definition.has("base_state") && definition.get("base_state").getAsJsonObject().has("Name") ? definition.get("base_state").getAsJsonObject().get("Name").getAsString() : null;
					def = new Definition(type, block_set_type, wood_type, color, tree, weathering_state, base_state);
				}

				List<BlockState> states = new ArrayList<>(e.getValue().getAsJsonObject().get("states").getAsJsonArray().size());
				for (var s : e.getValue().getAsJsonObject().get("states").getAsJsonArray()) {
					var state = s.getAsJsonObject();
					int stateId = state.get("id").getAsInt();
					BlockState blockState = new BlockState(stateId, block, new HashMap<>(), state.get("default") != null, noCollide.contains(stateId), def);
					if (state.get("properties") != null) for (var p : state.get("properties").getAsJsonObject().entrySet()) {
						blockState.properties.put(p.getKey(), p.getValue().getAsString());
					}
					if (blockState.isDefault) {
						defaultStates.put(block, blockState);
					}
					idsToStates.put(blockState.id, blockState);
					states.add(blockState);
				}
				blockStates.put(block, states);
			}

			noCollideIds = new boolean[idsToStates.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1];
			noCollide.forEach(i -> noCollideIds[i] = true);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Took " + (System.currentTimeMillis() - start) + " ms to load block states.");

	}

	public static record Definition(String type, String block_set_type, String wood_type, String color, String tree, String weathering_state, String base_state) {}

	public static record BlockState(int id, String block, Map<String, String> properties, boolean isDefault, boolean noCollision, Definition definition) {

		public String format() {
			return block + "" + properties.toString().replace("{", "[").replace("}", "]");
		}
	}

	/*
	 * Extract collidables from fabric.
	 JsonObject obj = new JsonObject();
		for (Block block : Registries.BLOCK) {
			Identifier identifier = Registries.BLOCK.getId(block);
			StateManager<Block, BlockState> stateManager = block.getStateManager();
			for (BlockState blockState : stateManager.getStates()) {
				List<String> states = new ArrayList<>();
				for (Property<?> property2 : stateManager.getProperties()) {
					states.add(property2.getName() + "=" + Util.getValueAsString(property2, blockState.get(property2)));
				}
				int id = Block.getRawIdFromState(blockState);
	//				System.out.println(identifier + " " + id + blockState.toString());
				if (checkLegalPos(blockState))
				obj.add(id+"", new JsonPrimitive(identifier.toString()));
			}
		}
		try {
			FileOutputStream out = new FileOutputStream("collides.json");
			out.write(new GsonBuilder().setPrettyPrinting().create().toJson(obj).getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean checkLegalPos(BlockState state) {
		try {
			VoxelShape shape = state.getCollisionShape(null, null);
			if (shape == VoxelShapes.fullCube()) {
				return false;
			}
			if (shape == VoxelShapes.empty() || shape.simplify().isEmpty()) {
				return true;
			}
		} catch (Exception e) {
			System.out.println(state);
		}
		return false;
	}
	 */
}
