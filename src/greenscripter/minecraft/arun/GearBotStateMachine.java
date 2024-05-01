package greenscripter.minecraft.arun;

import java.util.HashSet;
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
		setState(new FindWoodState());
	}

}

class FindWoodState extends PlayerState {

	static boolean[] logs = BlockStates.addTagToBlockSet(BlockStates.getBlockSet(), "minecraft:logs");
	static Set<Integer> logItems = new HashSet<>();
	static {
		for (String id : RegistryTags.itemTags.get("minecraft:logs")) {
			logItems.add(Slot.reverseItemRegistry.get(id));
		}
	}

	public FindWoodState() {
		onTick(e -> {
			e.swapToNow(new FindBlocksState(logs, false, e2 -> {
				InventoryData inv = e.value.getData(InventoryData.class);
				int count = OpenedScreen.countItems(s -> logItems.contains(s.itemId), inv.getActiveScreen().getInventoryIterator());
				System.out.println(e.value.name + " " + count);
				return count > 20;
			}));
		});
	}
}