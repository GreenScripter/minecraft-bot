package greenscripter.minecraft.play.inventory;

public class PlayerInventoryScreen extends OpenedScreen {

	public static void main(String[] args) {
		OpenedScreen sc = new OpenedScreen();
		sc.initWithSlotCount(39);
		for (int i = 0; i < 40; i++) {
			System.out.println(i + " " + sc.slotIds.get(sc.getOtherSlot(i)));
		}
	}

	public PlayerInventoryScreen() {
		windowId = 0;
		windowType = -1;
		slots = new Slot[46];
		for (int i = 0; i < slots.length; i++) {
			slots[i] = new Slot();
			slotIds.put(slots[i], i);
		}
		cursor = new Slot();
	}

	public Slot getOffhand() {
		return slots[slots.length - 1];
	}

	public int getSlotId(Slot slot) {
		return slotIds.get(slot);
	}

	public Slot getHotbarSlot(int index) {
		if (index >= 9) return null;
		//extra one to skip offhand
		return slots[slots.length - 2 - (8 - index)];
	}

	public int getOtherSlotsCount() {
		return slots.length - 36;
	}

	public Slot getOtherSlot(int index) {
		if (index >= getOtherSlotsCount()) return null;
		if (index == slots.length - 37) {
			return slots[45];
		}
		return slots[index];
	}

	public Slot getInventorySlot(int index) {
		if (index < 9) {
			return getHotbarSlot(index);
		}
		index -= 9;
		int last = slots.length - 9;
		int target = last - (27 - index);
		if (target > 36) return null;
		return slots[target - 1];
	}

	public Slot[] getArmor() {
		return new Slot[] { slots[5], slots[6], slots[7], slots[8] };
	}

}
