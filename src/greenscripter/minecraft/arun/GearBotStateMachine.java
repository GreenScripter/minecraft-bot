package greenscripter.minecraft.arun;

import java.util.List;
import java.util.Set;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.BlockStates;
import greenscripter.minecraft.packet.c2s.play.PlayerCommandPacket;
import greenscripter.minecraft.play.data.InventoryData;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.inventory.ItemId;
import greenscripter.minecraft.play.inventory.OpenedScreen;
import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.play.statemachine.BreakBlockState;
import greenscripter.minecraft.play.statemachine.PlayerMachine;
import greenscripter.minecraft.play.statemachine.PlayerState;
import greenscripter.minecraft.utils.Direction;
import greenscripter.minecraft.utils.Position;

public class GearBotStateMachine extends PlayerMachine {

	public GearBotStateMachine(ServerConnection t) {
		super(t);
		setState(new GearBotState());
	}

}

class GearBotState extends PlayerState {

	public GearBotState() {
		onTick(e -> {
			InventoryData inv = e.value.getData(InventoryData.class);
			if (FindWoodState.countLogs(inv.getActiveScreen()) < 5) {
				e.push(new FindWoodState());
			}
			if (OpenedScreen.countItems(ItemId.get("minecraft:crafting_table"), inv.getActiveScreen().getInventoryIterator()) < 2) {
				e.pushNow(new CraftTable());
			}

		});
	}
}

class CraftTable extends PlayerState {

	public CraftTable() {
		onTick(e -> {
			InventoryData inv = e.value.getData(InventoryData.class);

			OpenedScreen active = inv.getActiveScreen();
			List<Slot> logSlots = OpenedScreen.getSlotsMatching(s -> FindWoodState.logItems.contains(s.itemId), active.getInventoryIterator());
			//pickup logs
			inv.leftClickSlot(logSlots.get(0));
			//place 1 log in crafting grid
			inv.rightClickSlot(active.getOtherSlot(1));
			//put logs back
			inv.leftClickSlot(logSlots.get(0));
			//pick up planks
			inv.leftClickSlot(active.getOtherSlot(0));

			//place crafting table recipe
			inv.rightClickSlot(active.getOtherSlot(1));
			inv.rightClickSlot(active.getOtherSlot(2));
			inv.rightClickSlot(active.getOtherSlot(3));
			inv.rightClickSlot(active.getOtherSlot(4));

			//pick up table
			inv.leftClickSlot(active.getOtherSlot(0));

			List<Slot> slots = OpenedScreen.getSlotsMatching(s -> s.present && s.itemId == ItemId.get("minecraft:crafting_table"), active.getInventoryIterator());
			if (slots.isEmpty()) {
				slots = OpenedScreen.getEmptySlots(active.getInventoryIterator());
			}
			if (slots.isEmpty()) {
				inv.dropAllCursorItems();
			} else {
				inv.leftClickSlot(slots.get(0));
			}
			e.pop();
		});
	}
}

class PlaceCraftingTableState extends PlayerState {

	public PlaceCraftingTableState() {
		onTick(e -> {
			PositionData pos = e.value.getData(PositionData.class);
			WorldData world = e.value.getData(WorldData.class);
			Position over = new Position(pos.pos).add(0, 2, 0);
			if (world.world.getBlock(over) == BlockStates.getDefaultBlockState("minecraft:crafting_table").id()) {
				e.pop();
			}
			if (world.world.getBlock(over) != BlockStates.getDefaultBlockState("minecraft:air").id()) {
				e.push(new BreakBlockState(GearBot.render, over));
			}
			InventoryData inv = e.value.getData(InventoryData.class);
			OpenedScreen active = inv.getActiveScreen();

			List<Slot> slots = OpenedScreen.getSlotsMatching(s -> s.present && s.itemId == ItemId.get("minecraft:crafting_table"), active.getInventoryIterator());
			if (slots.isEmpty()) e.pop();
			int hotbarSlot = active.getHotbarIndex(slots.get(0));
			if (hotbarSlot == -1) {
				inv.swapSlots(slots.get(0), 4);
				hotbarSlot = 4;
			}

			inv.setHotbarSlot(hotbarSlot);

			e.value.sendPacket(new PlayerCommandPacket(PlayerCommandPacket.START_SNEAKING));
			world.useItemOn(e.value, 0, new Position(pos.pos).add(0, 2, 0), Direction.UP.ordinal());
			e.value.sendPacket(new PlayerCommandPacket(PlayerCommandPacket.STOP_SNEAKING));
		});
	}
}

class FindWoodState extends FindBlocksState {

	static boolean[] logs = BlockStates.addTagToBlockSet(BlockStates.getBlockSet(), "minecraft:logs");
	static Set<Integer> logItems = ItemId.tags("minecraft:logs");

	public static int countLogs(OpenedScreen inv) {
		return OpenedScreen.countItems(s -> logItems.contains(s.itemId), inv.getInventoryIterator());
	}

	public FindWoodState() {
		super(logs, false, e2 -> {
			InventoryData inv = e2.value.getData(InventoryData.class);
			int count = countLogs(inv.getActiveScreen());
			return count > 20;
		});
	}
}