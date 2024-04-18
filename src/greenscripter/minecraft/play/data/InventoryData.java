package greenscripter.minecraft.play.data;

import greenscripter.minecraft.play.inventory.OpenedScreen;
import greenscripter.minecraft.play.inventory.PlayerInventoryScreen;

public class InventoryData extends PlayData {

	public PlayerInventoryScreen inv = new PlayerInventoryScreen();
	public OpenedScreen screen;
	public int hotbarSlot;

	public OpenedScreen getActiveScreen() {
		if (screen == null) {
			return inv;
		}
		return screen;
	}
}
