package greenscripter.minecraft.gameinfo;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class Registries {

	public static void main(String[] args) {
		System.out.println(registries.get("minecraft:data_component_type"));
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
		safeAttack = new boolean[Registries.registries.get("minecraft:entity_type").values().stream().max(Integer::compareTo).orElse(0) + 1];
		for (int i = 0; i < safeAttack.length; i++) {
			safeAttack[i] = true;
		}
		for (int i = 0; i < kickIfHit.length; i++) {
			safeAttack[Registries.registries.get("minecraft:entity_type").get(kickIfHit[i])] = false;
		}
		try {
			String registriesString = ResourceExtractor.getJSON("greenscripter/minecraft/resources/itemInfo.json");
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
		public int maxDurability;
		public boolean isFood;
		public int hunger;
		public float saturation;
		public boolean alwaysEdible;
		public boolean isDamageable;
		public boolean isTool;
		public float toolSpeed;
		public int toolLevel;
		public String toolType;
		public boolean isArmor;
		public int armorProtection;
		public float armorToughness;
		public String armorSlot;
		public double attackSpeed;
		public double attackDamage;
		public boolean isblock;
		public String blockId;

		public String toString() {
			return "ItemInfo [nestable=" + nestable //
					+ ", maxStack=" + maxStack //
					+ ", maxDurability=" + maxDurability //
					+ ", isFood=" + isFood + ", hunger=" + hunger + ", saturation=" + saturation + ", alwaysEdible=" + alwaysEdible //
					+ ", isDamageable=" + isDamageable //
					+ ", isTool=" + isTool + ", toolSpeed=" + toolSpeed + ", toolLevel=" + toolLevel + ", " + (toolType != null ? "toolType=" + toolType + ", " : "") //
					+ "isArmor=" + isArmor + ", armorProtection=" + armorProtection + ", armorToughness=" + armorToughness + ", " + (armorSlot != null ? "armorSlot=" + armorSlot + ", " : "") //
					+ "isblock=" + isblock + ", " + (blockId != null ? "blockId=" + blockId : "") + "]";
		}

	}
	/*Extract item information from fabric
		JsonObject allItems = new JsonObject();
		for (var item : Registries.ITEM) {
			Identifier identifier = Registries.ITEM.getId(item);
			JsonObject itemInfo = new JsonObject();
			itemInfo.add("nestable", new JsonPrimitive(item.canBeNested()));
			itemInfo.add("maxStack", new JsonPrimitive(item.getMaxCount()));
			itemInfo.add("maxDurability", new JsonPrimitive(item.getMaxDamage()));
			itemInfo.add("isFood", new JsonPrimitive(item.isFood()));
			if (item.isFood()) {
				itemInfo.add("hunger", new JsonPrimitive(item.getFoodComponent().getHunger()));
				itemInfo.add("saturation", new JsonPrimitive(item.getFoodComponent().getSaturationModifier()));
				itemInfo.add("alwaysEdible", new JsonPrimitive(item.getFoodComponent().isAlwaysEdible()));
			}
			itemInfo.add("isDamageable", new JsonPrimitive(item.isDamageable()));
			itemInfo.add("isTool", new JsonPrimitive(item instanceof ToolItem));
	
			if (item instanceof ToolItem tool) {
				float speed = (tool).getMaterial().getMiningSpeedMultiplier();
				itemInfo.add("toolSpeed", new JsonPrimitive(speed));
				int level = (tool).getMaterial().getMiningLevel();
				itemInfo.add("toolLevel", new JsonPrimitive(level));
				if (tool instanceof PickaxeItem) {
					itemInfo.add("toolType", new JsonPrimitive("pickaxe"));
				}
				if (tool instanceof AxeItem) {
					itemInfo.add("toolType", new JsonPrimitive("axe"));
				}
				if (tool instanceof HoeItem) {
					itemInfo.add("toolType", new JsonPrimitive("hoe"));
				}
				if (tool instanceof ShovelItem) {
					itemInfo.add("toolType", new JsonPrimitive("shovel"));
				}
				if (tool instanceof SwordItem) {
					itemInfo.add("toolType", new JsonPrimitive("sword"));
				}
			}
			itemInfo.add("isArmor", new JsonPrimitive(item instanceof ArmorItem));
	
			if (item instanceof ArmorItem armor) {
				itemInfo.add("armorProtection", new JsonPrimitive(armor.getProtection()));
				itemInfo.add("armorToughness", new JsonPrimitive(armor.getToughness()));
				itemInfo.add("armorSlot", new JsonPrimitive(armor.getSlotType().name()));
			}
			itemInfo.add("attackSpeed", new JsonPrimitive(4+item.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_SPEED).stream().mapToDouble(m -> m.getValue()).sum()));
			itemInfo.add("attackDamage", new JsonPrimitive(1+item.getAttributeModifiers(EquipmentSlot.MAINHAND).get(EntityAttributes.GENERIC_ATTACK_DAMAGE).stream().mapToDouble(m -> m.getValue()).sum()));
	
			itemInfo.add("isBlock", new JsonPrimitive(item instanceof BlockItem));
	
			if (item instanceof BlockItem block) {
				itemInfo.add("blockId", new JsonPrimitive(Registries.BLOCK.getId(block.getBlock()) + ""));
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
