package greenscripter.minecraft.gameinfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.play.inventory.Components;
import greenscripter.minecraft.play.inventory.components.AttributeModifiersComponent;
import greenscripter.minecraft.play.inventory.components.BannerPatternsComponent;
import greenscripter.minecraft.play.inventory.components.BaseColorComponent;
import greenscripter.minecraft.play.inventory.components.BeesComponent;
import greenscripter.minecraft.play.inventory.components.BlockEntityDataComponent;
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
		start = System.currentTimeMillis();
	}
}
