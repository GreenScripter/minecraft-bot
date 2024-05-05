package greenscripter.minecraft.play.statemachine;

import greenscripter.minecraft.gameinfo.BlockStates;
import greenscripter.minecraft.gameinfo.RegistryTags;
import greenscripter.minecraft.play.data.InventoryData;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.inventory.ItemId;
import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.utils.Vector;
import greenscripter.remoteindicators.IndicatorServer;

public class BreakBlockState extends PlayerState {

	public IndicatorServer render;
	int shapeId;

	public BreakBlockState(Position block) {
		this(null, block);
	}

	public BreakBlockState(IndicatorServer render, Position block) {
		this.render = render;
		onInit(e -> {
			WorldData data = e.value.getData(WorldData.class);
			PositionData pos = e.value.getData(PositionData.class);
			if (render != null) shapeId = render.addCuboid(data.world.id, new Vector(block.x, block.y, block.z), new Vector(block.x + 1, block.y + 1, block.z + 1), IndicatorServer.getColor(255, 0, 0, 255));
			InventoryData inv = e.value.getData(InventoryData.class);
			String target = getBlockItemTag(data.world.getBlock(block));
			Slot current = inv.getActiveScreen().getHotbarSlot(inv.hotbarSlot);
			if ((target == null && (!current.present || !current.getItemInfo().isTool)) //
					|| (target != null && current.present && ItemId.tags(target).contains(current.itemId))) {
				//slot already reasonable
			} else {
				var it = inv.getActiveScreen().getHotbarIterator();
				while (it.hasNext()) {
					var next = it.next();
					if (target == null) {
						if (!next.present || !next.getItemInfo().isTool) {
							inv.setHotbarSlot(inv.getActiveScreen().getHotbarIndex(next));
							break;
						}
					} else {
						if (next.present && ItemId.tags(target).contains(next.itemId)) {
							inv.setHotbarSlot(inv.getActiveScreen().getHotbarIndex(next));
							break;
						}
					}
				}
			}
			data.startBreaking(e.value, block);
			data.finishBreaking(e.value, block);
			if (pos.getEyePos().distanceTo(new Vector(block)) >= 6) {
				e.pop();
			}
		});
		onTick(e -> {
			WorldData data = e.value.getData(WorldData.class);
			PositionData pos = e.value.getData(PositionData.class);

			if (pos.getEyePos().distanceTo(new Vector(block)) >= 6) {
				e.pop();
			}
			//			if (e.value.id == 0) System.out.println(data.world.getBlock(block));
			if (data.finishBreaking(e.value, block)) {
				e.popNow();
			}
		});
		onFinished(e -> {
			if (render != null) {
				render.removeShape(shapeId);
			}
		});

	}

	public static String getBlockItemTag(int blockId) {
		String block = BlockStates.getState(blockId).block();
		if (RegistryTags.matchesBlockTag("minecraft:mineable_pickaxe", block)) {
			return "minecraft:pickaxes";
		}
		if (RegistryTags.matchesBlockTag("minecraft:mineable_shovel", block)) {
			return "minecraft:shovels";
		}
		if (RegistryTags.matchesBlockTag("minecraft:mineable_axe", block)) {
			return "minecraft:axes";
		}
		if (RegistryTags.matchesBlockTag("minecraft:mineable_hoe", block)) {
			return "minecraft:hoes";
		}
		if (RegistryTags.matchesBlockTag("minecraft:sword_efficient", block)) {
			return "minecraft:swords";
		}
		return null;
	}
}
