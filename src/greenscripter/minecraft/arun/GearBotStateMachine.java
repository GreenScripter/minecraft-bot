package greenscripter.minecraft.arun;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.BlockStates;
import greenscripter.minecraft.gameinfo.RegistryTags;
import greenscripter.minecraft.play.data.InventoryData;
import greenscripter.minecraft.play.inventory.OpenedScreen;
import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.play.statemachine.PlayerMachine;
import greenscripter.minecraft.play.statemachine.PlayerState;

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
			List<Slot> logSlots = OpenedScreen.getSlotsMatching(s -> FindWoodState.logItems.contains(s.itemId), inv.getActiveScreen().getInventoryIterator());
			System.out.println(logSlots);
			logSlots.forEach(s -> inv.getActiveScreen().getSlotId(s));
			inv.leftClickSlot(e.value, logSlots.get(0));
			inv.leftClickSlot(e.value, inv.getActiveScreen().getOtherSlot(1));
			inv.shiftClickSlot(e.value, inv.getActiveScreen().getOtherSlot(0));
		});
	}
}

class FindWoodState extends FindBlocksState {

	static boolean[] logs = BlockStates.addTagToBlockSet(BlockStates.getBlockSet(), "minecraft:logs");
	static Set<Integer> logItems = new HashSet<>();
	static {
		for (String id : RegistryTags.itemTags.get("minecraft:logs")) {
			logItems.add(Slot.reverseItemRegistry.get(id));
		}
	}

	public static int countLogs(OpenedScreen inv) {
		return OpenedScreen.countItems(s -> logItems.contains(s.itemId), inv.getInventoryIterator());
	}

	public FindWoodState() {
		super(logs, false, e2 -> {
			InventoryData inv = e2.value.getData(InventoryData.class);
			int count = countLogs(inv.getActiveScreen());
			return count > 5;
		});
	}
}