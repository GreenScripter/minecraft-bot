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

	public static Map<String, Map<String, List<String>>> blockProperties = new HashMap<>();
	public static Map<String, BlockState> defaultStates = new HashMap<>();
	public static Map<String, List<BlockState>> blockStates = new HashMap<>();
	public static Map<Integer, BlockState> idsToStates = new HashMap<>();
	public static boolean[] noCollideIds;

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
			String collidesString = ResourceExtractor.getJSON("greenscripter/minecraft/resources/collides.json");
			Map<String, JsonElement> collides = JsonParser.parseString(collidesString).getAsJsonObject().asMap();
			Set<Integer> noCollide = new HashSet<>(collides.keySet().stream().map(i -> Integer.parseInt(i)).toList());

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
					int stateId = state.get("id").getAsInt();
					BlockState blockState = new BlockState(stateId, block, new HashMap<>(), state.get("default") != null, noCollide.contains(stateId));
					if (state.get("properties") != null) for (var p : state.get("properties").getAsJsonObject().entrySet()) {
						blockState.properties.put(p.getKey(), p.getValue().getAsString());
					}
					if (blockState.isDefault) {
						defaultStates.put(block, blockState);
					}
					idsToStates.put(blockState.id, blockState);
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

	public static record BlockState(int id, String block, Map<String, String> properties, boolean isDefault, boolean noCollision) {

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