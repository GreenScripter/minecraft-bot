package greenscripter.minecraft.gameinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import greenscripter.minecraft.gameinfo.BlockStates.BlockState;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.play.inventory.Components;
import greenscripter.minecraft.play.inventory.ItemId;
import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.play.inventory.components.AttributeModifiersComponent;
import greenscripter.minecraft.play.inventory.components.AttributeModifiersComponent.Attribute;
import greenscripter.minecraft.play.inventory.components.BannerPatternsComponent;
import greenscripter.minecraft.play.inventory.components.BaseColorComponent;
import greenscripter.minecraft.play.inventory.components.BeesComponent;
import greenscripter.minecraft.play.inventory.components.BlockEntityDataComponent;
import greenscripter.minecraft.play.inventory.components.BlockPredicate.BlockSet;
import greenscripter.minecraft.play.inventory.components.BlockStateComponent;
import greenscripter.minecraft.play.inventory.components.BucketEntityDataComponent;
import greenscripter.minecraft.play.inventory.components.BundleContentsComponent;
import greenscripter.minecraft.play.inventory.components.CanBreakComponent;
import greenscripter.minecraft.play.inventory.components.CanPlaceOnComponent;
import greenscripter.minecraft.play.inventory.components.ChargedProjectilesComponent;
import greenscripter.minecraft.play.inventory.components.ContainerComponent;
import greenscripter.minecraft.play.inventory.components.ContainerLootComponent;
import greenscripter.minecraft.play.inventory.components.CreativeSlotLockComponent;
import greenscripter.minecraft.play.inventory.components.CustomDataComponent;
import greenscripter.minecraft.play.inventory.components.CustomModelDataComponent;
import greenscripter.minecraft.play.inventory.components.CustomNameComponent;
import greenscripter.minecraft.play.inventory.components.DamageComponent;
import greenscripter.minecraft.play.inventory.components.DebugStickStateComponent;
import greenscripter.minecraft.play.inventory.components.DyedColorComponent;
import greenscripter.minecraft.play.inventory.components.EnchantmentGlintOverrideComponent;
import greenscripter.minecraft.play.inventory.components.EnchantmentsComponent;
import greenscripter.minecraft.play.inventory.components.EntityDataComponent;
import greenscripter.minecraft.play.inventory.components.FireResistantComponent;
import greenscripter.minecraft.play.inventory.components.FireworkExplosionComponent;
import greenscripter.minecraft.play.inventory.components.FireworksComponent;
import greenscripter.minecraft.play.inventory.components.FoodComponent;
import greenscripter.minecraft.play.inventory.components.FoodComponent.Effect;
import greenscripter.minecraft.play.inventory.components.HideAdditionalTooltipComponent;
import greenscripter.minecraft.play.inventory.components.HideTooltipComponent;
import greenscripter.minecraft.play.inventory.components.InstrumentComponent;
import greenscripter.minecraft.play.inventory.components.IntangibleProjectileComponent;
import greenscripter.minecraft.play.inventory.components.ItemNameComponent;
import greenscripter.minecraft.play.inventory.components.JukeboxPlayableComponent;
import greenscripter.minecraft.play.inventory.components.LockComponent;
import greenscripter.minecraft.play.inventory.components.LodestoneTrackerComponent;
import greenscripter.minecraft.play.inventory.components.LoreComponent;
import greenscripter.minecraft.play.inventory.components.MapColorComponent;
import greenscripter.minecraft.play.inventory.components.MapDecorationsComponent;
import greenscripter.minecraft.play.inventory.components.MapIDComponent;
import greenscripter.minecraft.play.inventory.components.MapPostProcessingComponent;
import greenscripter.minecraft.play.inventory.components.MaxDamageComponent;
import greenscripter.minecraft.play.inventory.components.MaxStackSizeComponent;
import greenscripter.minecraft.play.inventory.components.NoteBlockSoundComponent;
import greenscripter.minecraft.play.inventory.components.OminousBottleAmplifierComponent;
import greenscripter.minecraft.play.inventory.components.PotDecorationsComponent;
import greenscripter.minecraft.play.inventory.components.PotionContentsComponent;
import greenscripter.minecraft.play.inventory.components.PotionEffect;
import greenscripter.minecraft.play.inventory.components.ProfileComponent;
import greenscripter.minecraft.play.inventory.components.RarityComponent;
import greenscripter.minecraft.play.inventory.components.RecipesComponent;
import greenscripter.minecraft.play.inventory.components.RepairCostComponent;
import greenscripter.minecraft.play.inventory.components.StoredEnchantmentsComponent;
import greenscripter.minecraft.play.inventory.components.SuspiciousStewEffectsComponent;
import greenscripter.minecraft.play.inventory.components.ToolComponent;
import greenscripter.minecraft.play.inventory.components.TrimComponent;
import greenscripter.minecraft.play.inventory.components.UnbreakableComponent;
import greenscripter.minecraft.play.inventory.components.WritableBookContentComponent;
import greenscripter.minecraft.play.inventory.components.WrittenBookContentComponent;

public class ComponentData {

	public static Integer get(String name) {
		return Registries.get("minecraft:data_component_type", name);
	}

	public static String get(Integer id) {
		return Registries.get("minecraft:data_component_type", id);
	}

	public static Component getComponent(Integer id) {
		var source = componentTypes.get(id);
		if (source == null) return null;
		return source.get();
	}

	public static Map<Integer, Components> defaultComponents = new HashMap<>();

	public static Map<Integer, Supplier<Component>> componentTypes = new HashMap<>();

	private static void registerType(Supplier<Component> source) {
		Component c = source.get();
		componentTypes.put(c.id(), source);
	}

	static {
		long start = System.currentTimeMillis();
		registerType(AttributeModifiersComponent::new);
		registerType(BannerPatternsComponent::new);
		registerType(BaseColorComponent::new);
		registerType(BeesComponent::new);
		registerType(BlockEntityDataComponent::new);
		registerType(BlockStateComponent::new);
		registerType(BucketEntityDataComponent::new);
		registerType(BundleContentsComponent::new);
		registerType(CanBreakComponent::new);
		registerType(CanPlaceOnComponent::new);
		registerType(ChargedProjectilesComponent::new);
		registerType(ContainerComponent::new);
		registerType(ContainerLootComponent::new);
		registerType(CreativeSlotLockComponent::new);
		registerType(CustomDataComponent::new);
		registerType(CustomModelDataComponent::new);
		registerType(CustomNameComponent::new);
		registerType(DamageComponent::new);
		registerType(DebugStickStateComponent::new);
		registerType(DyedColorComponent::new);
		registerType(EnchantmentGlintOverrideComponent::new);
		registerType(EnchantmentsComponent::new);
		registerType(EntityDataComponent::new);
		registerType(FireResistantComponent::new);
		registerType(FireworkExplosionComponent::new);
		registerType(FireworksComponent::new);
		registerType(FoodComponent::new);
		registerType(HideAdditionalTooltipComponent::new);
		registerType(HideTooltipComponent::new);
		registerType(InstrumentComponent::new);
		registerType(IntangibleProjectileComponent::new);
		registerType(ItemNameComponent::new);
		registerType(JukeboxPlayableComponent::new);
		registerType(LockComponent::new);
		registerType(LodestoneTrackerComponent::new);
		registerType(LoreComponent::new);
		registerType(MapColorComponent::new);
		registerType(MapDecorationsComponent::new);
		registerType(MapIDComponent::new);
		registerType(MapPostProcessingComponent::new);
		registerType(MaxDamageComponent::new);
		registerType(MaxStackSizeComponent::new);
		registerType(NoteBlockSoundComponent::new);
		registerType(OminousBottleAmplifierComponent::new);
		registerType(PotDecorationsComponent::new);
		registerType(PotionContentsComponent::new);
		registerType(ProfileComponent::new);
		registerType(RarityComponent::new);
		registerType(RecipesComponent::new);
		registerType(RepairCostComponent::new);
		registerType(StoredEnchantmentsComponent::new);
		registerType(SuspiciousStewEffectsComponent::new);
		registerType(ToolComponent::new);
		registerType(TrimComponent::new);
		registerType(UnbreakableComponent::new);
		registerType(WritableBookContentComponent::new);
		registerType(WrittenBookContentComponent::new);
		System.out.println("Took " + (System.currentTimeMillis() - start) + " ms to load all component types.");

		ItemId.get("minecraft:dirt"); // Make sure item ids are already loaded for timing purposes.
		BlockStates.getState(0);

		start = System.currentTimeMillis();
		try {
			String registriesString = ResourceExtractor.getJSON("greenscripter/minecraft/resources/reports/items.json");
			Map<String, JsonElement> items = JsonParser.parseString(registriesString).getAsJsonObject().asMap();
			for (var e : items.entrySet()) {
				Components components = new Components();
				parseDefaultComponents(components, e.getValue().getAsJsonObject());
				defaultComponents.put(ItemId.get(e.getKey()), components);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Took " + (System.currentTimeMillis() - start) + " ms to load default components ids.");
	}

	@SuppressWarnings("unused")
	private static void parseDefaultComponents(Components cs, JsonObject element) {
		Map<String, JsonElement> entries = element.get("components").getAsJsonObject().asMap();

		for (var e : entries.entrySet()) {
			Component c = getComponent(get(e.getKey()));
			if (c instanceof RarityComponent sc) {
				sc.rarity = RarityComponent.getRarity(e.getValue().getAsString());
			} else if (c instanceof RepairCostComponent sc) {
				sc.repairCost = e.getValue().getAsInt();
			} else if (c instanceof MaxStackSizeComponent sc) {
				sc.maxStackSize = e.getValue().getAsInt();
			} else if (c instanceof MaxDamageComponent sc) {
				sc.maxDamage = e.getValue().getAsInt();
			} else if (c instanceof MapPostProcessingComponent sc) {
				sc.type = e.getValue().getAsInt();
			} else if (c instanceof DamageComponent sc) {
				sc.damage = e.getValue().getAsInt();
			} else if (c instanceof EnchantmentGlintOverrideComponent sc) {
				sc.hasGlint = e.getValue().getAsBoolean();
			} else if (c instanceof OminousBottleAmplifierComponent sc) {
				sc.amplifier = e.getValue().getAsInt();
			} else if (c instanceof MapColorComponent sc) {
				sc.color = e.getValue().getAsInt();
			} else if (c instanceof LoreComponent sc) {
				if (e.getValue().getAsJsonArray().size() > 0) {
					System.err.println("Error loading default lore, has " + e.getValue() + ", expected []");
				}
			} else if (c instanceof ChargedProjectilesComponent sc) {
				if (e.getValue().getAsJsonArray().size() > 0) {
					System.err.println("Error loading default charged projectiles, has " + e.getValue() + ", expected []");
				}
			} else if (c instanceof BannerPatternsComponent sc) {
				if (e.getValue().getAsJsonArray().size() > 0) {
					System.err.println("Error loading default banner patterns, has " + e.getValue() + ", expected []");
				}
			} else if (c instanceof ContainerComponent sc) {
				if (e.getValue().getAsJsonArray().size() > 0) {
					System.err.println("Error loading default container, has " + e.getValue() + ", expected []");
				}
			} else if (c instanceof BundleContentsComponent sc) {
				if (e.getValue().getAsJsonArray().size() > 0) {
					System.err.println("Error loading default bundle contents, has " + e.getValue() + ", expected []");
				}
			} else if (c instanceof BeesComponent sc) {
				if (e.getValue().getAsJsonArray().size() > 0) {
					System.err.println("Error loading default bees, has " + e.getValue() + ", expected []");
				}
			} else if (c instanceof LoreComponent sc) {
				if (e.getValue().getAsJsonArray().size() > 0) {
					System.err.println("Error loading default lore, has " + e.getValue() + ", expected []");
				}
			} else if (c instanceof RecipesComponent sc) {
				if (e.getValue().getAsJsonArray().size() > 0) {
					System.err.println("Error loading default recipies, has " + e.getValue() + ", expected []");
				}
			} else if (c instanceof SuspiciousStewEffectsComponent sc) {
				if (e.getValue().getAsJsonArray().size() > 0) {
					System.err.println("Error loading default suspicious stew, has " + e.getValue() + ", expected []");
				}
			} else if (c instanceof EnchantmentsComponent sc) {
				if (e.getValue().getAsJsonObject().get("levels").getAsJsonObject().size() > 0) {
					System.err.println("Error loading default enchantments, has " + e.getValue() + ", expected {\"levels\":[]}");
				}
			} else if (c instanceof StoredEnchantmentsComponent sc) {
				if (e.getValue().getAsJsonObject().get("levels").getAsJsonObject().size() > 0) {
					System.err.println("Error loading default stored Ienchantments, has " + e.getValue() + ", expected {\"levels\":[]}");
				}
			} else if (c instanceof FireResistantComponent sc) {
				if (e.getValue().getAsJsonObject().size() > 0) {
					System.err.println("Error loading default fire resistant, has " + e.getValue() + ", expected {}");
				}
			} else if (c instanceof WritableBookContentComponent sc) {
				if (e.getValue().getAsJsonObject().size() > 0) {
					System.err.println("Error loading default writable book, has " + e.getValue() + ", expected {}");
				}
			} else if (c instanceof MapDecorationsComponent sc) {
				if (e.getValue().getAsJsonObject().size() > 0) {
					System.err.println("Error loading default map decorations, has " + e.getValue() + ", expected {}");
				}
			} else if (c instanceof DebugStickStateComponent sc) {
				if (e.getValue().getAsJsonObject().size() > 0) {
					System.err.println("Error loading default debug stick, has " + e.getValue() + ", expected {}");
				}
			} else if (c instanceof BucketEntityDataComponent sc) {
				if (e.getValue().getAsJsonObject().size() > 0) {
					System.err.println("Error loading default bucket entity, has " + e.getValue() + ", expected {}");
				}
			} else if (c instanceof PotionContentsComponent sc) {
				if (e.getValue().getAsJsonObject().size() > 0) {
					System.err.println("Error loading default potion content, has " + e.getValue() + ", expected {}");
				}
			} else if (c instanceof JukeboxPlayableComponent sc) {
				if (e.getValue().getAsJsonObject().size() > 1) {
					System.err.println("Error loading default bucket entity, has " + e.getValue() + ", expected {\"song\":\"<songid>\"}");
				}
				sc.songType = 0;
				sc.songName = e.getValue().getAsJsonObject().get("song").getAsString();
			} else if (c instanceof FireworksComponent sc) {
				if (e.getValue().getAsJsonObject().size() > 1) {
					System.err.println("Error loading default firework, has " + e.getValue() + ", expected {\"flight_duration\":\"<flight_duration>\"}");
				}
				sc.flightDuration = e.getValue().getAsJsonObject().get("flight_duration").getAsInt();
			} else if (c instanceof PotDecorationsComponent sc) {
				if (e.getValue().getAsJsonArray().size() > 4) {
					System.err.println("Error loading default pot decorations, has " + e.getValue() + ", expected [<ids>]");
				}
				for (var idj : e.getValue().getAsJsonArray().asList()) {
					sc.decorationIds.add(ItemId.get(idj.getAsString()));
				}
			} else if (c instanceof AttributeModifiersComponent sc) {
				var array = e.getValue().getAsJsonObject().get("modifiers").getAsJsonArray();

				for (var modifier : array) {
					var o = modifier.getAsJsonObject();
					Attribute a = new Attribute();
					if (o.has("operation") && o.get("operation").getAsString().equals("add_value")) {
						a.operation = Attribute.OPERATION_ADD;
						o.remove("operation");
					}
					if (o.has("slot") && o.get("slot").getAsString().equals("mainhand")) {
						a.slot = Attribute.SLOT_MAIN_HAND;
						o.remove("slot");
					}
					if (o.has("amount")) {
						a.value = o.get("amount").getAsDouble();
						o.remove("amount");
					}
					if (o.has("type")) {
						a.typeId = Registries.get("minecraft:attribute", o.get("type").getAsString());
						o.remove("type");
					}
					if (o.has("id")) {
						a.name = o.get("id").getAsString();
						a.uuid = UUID.nameUUIDFromBytes(a.name.getBytes());
						o.remove("id");
					}
					sc.attributes.add(a);
					if (o.size() > 0) {
						System.err.println("Error loading default Attribute Modifiers modifier, has " + o + " extra data, expected {}");
					}
				}

				e.getValue().getAsJsonObject().remove("modifiers");
				if (e.getValue().getAsJsonObject().size() > 0) {
					System.err.println("Error loading default Attribute Modifiers, has " + e.getValue() + " extra data, expected {}");
				}
			} else if (c instanceof FoodComponent sc) {
				var food = e.getValue().getAsJsonObject();
				sc.nutrition = food.get("nutrition").getAsInt();
				sc.saturation = food.get("saturation").getAsFloat();
				if (food.has("can_always_eat")) sc.canAlwaysEat = food.get("can_always_eat").getAsBoolean();
				if (food.has("eat_seconds")) sc.secondsToEat = food.get("eat_seconds").getAsFloat();

				food.remove("nutrition");
				food.remove("saturation");
				food.remove("can_always_eat");
				food.remove("eat_seconds");

				if (food.has("using_converts_to")) {
					var converts = food.get("using_converts_to").getAsJsonObject();
					Slot s = new Slot();
					s.itemCount = 1;
					s.present = true;
					s.itemId = ItemId.get(converts.get("id").getAsString());

					sc.convertsTo = s;
					converts.remove("id");
					if (!converts.isEmpty()) {
						System.err.println("Error loading default food, has " + converts + " extra data in using_converts_to, expected {}");
					}
				}
				food.remove("using_converts_to");

				if (food.has("effects")) {
					for (var potion : food.get("effects").getAsJsonArray()) {
						var effects = potion.getAsJsonObject();
						var effect = effects.get("effect").getAsJsonObject();
						Effect eff = new Effect();
						if (effects.has("probability")) eff.probability = effects.get("probability").getAsFloat();
						eff.effect = new PotionEffect();
						eff.effect.typeId = Registries.get("minecraft:mob_effect", effect.get("id").getAsString());
						eff.effect.detail = new PotionEffect.Detail();
						if (effects.has("amplifier")) eff.effect.detail.amplifier = effects.get("amplifier").getAsInt();
						if (effects.has("duration")) eff.effect.detail.duration = effects.get("duration").getAsInt();
						if (effects.has("show_icon")) eff.effect.detail.showIcon = effects.get("show_icon").getAsBoolean();
						effect.remove("probability");
						effect.remove("id");
						effect.remove("amplifier");
						effect.remove("duration");
						effect.remove("show_icon");
						if (!effect.isEmpty()) {
							System.err.println("Error loading default food effects, has " + e.getValue() + " extra data, expected {}");
						}

					}
				}
				food.remove("effects");

				if (!food.isEmpty()) {
					System.err.println("Error loading default food, has " + e.getValue() + " extra data, expected {}");
				}
			} else if (c instanceof ToolComponent sc) {
				var tool = e.getValue().getAsJsonObject();
				var array = tool.get("rules").getAsJsonArray();

				for (var rule : array) {
					var o = rule.getAsJsonObject();
					ToolComponent.Rule ru = new ToolComponent.Rule();
					if (o.has("correct_for_drops")) ru.correctDrop = o.get("correct_for_drops").getAsBoolean();
					if (o.has("speed")) ru.speed = o.get("speed").getAsFloat();
					if (o.has("blocks")) {
						var blocks = o.get("blocks");
						ru.blocks = new BlockSet();
						if (blocks.isJsonPrimitive()) {
							if (blocks.getAsString().startsWith("#")) {
								ru.blocks.tagName = blocks.getAsString();
							} else {
								var states = BlockStates.getBlockStates(blocks.getAsString());
								ru.blocks.blockIds = new int[states.size()];
								for (int i = 0; i < states.size(); i++) {
									ru.blocks.blockIds[i] = states.get(i).id();
								}

							}
							o.remove("blocks");
						} else if (blocks.isJsonArray()) {
							List<BlockState> states = new ArrayList<>();
							for (var el : blocks.getAsJsonArray()) {
								states.addAll(BlockStates.getBlockStates(el.getAsString()));
							}
							ru.blocks.blockIds = new int[states.size()];
							for (int i = 0; i < states.size(); i++) {
								ru.blocks.blockIds[i] = states.get(i).id();
							}
							o.remove("blocks");
						} else {
							System.err.println("Error loading default unable to understand blockset rule, has " + blocks + " extra data, expected {}");
						}
					}

					o.remove("correct_for_drops");
					o.remove("speed");
					sc.rules.add(ru);
					if (o.size() > 0) {
						System.err.println("Error loading default tool component rule, has " + o + " extra data, expected {}");
					}
				}
				if (tool.has("damage_per_block")) sc.damagePerBlock = tool.get("damage_per_block").getAsInt();

				tool.remove("rules");
				tool.remove("damage_per_block");
				if (tool.size() > 0) {
					System.err.println("Error loading default tool components, has " + e.getValue() + " extra data, expected {}");
				}
			} else {
				System.err.println(c.getClass() + " " + c);
				System.err.println(e.getValue());
			}
			cs.setComponent(c);

		}
	}
}
