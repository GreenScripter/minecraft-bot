package greenscripter.minecraft.play.inventory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import greenscripter.minecraft.gameinfo.Registries;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.utils.IndexIterator;

public class OpenedScreen {

	public NBTComponent title;
	public int windowId;
	public int windowType;
	public int stateId;
	public Slot[] slots;
	public Slot cursor;
	public Map<Slot, Integer> slotIds = new HashMap<>();
	public Map<Short, Short> properties = new HashMap<>();

	public boolean isInitialized() {
		return slots != null;
	}

	public void initWithSlotCount(int count) {
		slots = new Slot[count];
		slotIds.clear();
		for (int i = 0; i < count; i++) {
			slots[i] = new Slot();
			slotIds.put(slots[i], i);
		}
		cursor = new Slot();
	}

	public void replicateInvFrom(OpenedScreen other) {
		for (int i = 0; i < 36; i++) {
			getInventorySlot(i).become(other.getInventorySlot(i));
		}
	}

	public int getSlotId(Slot slot) {
		return slotIds.get(slot);
	}

	public Slot getHotbarSlot(int index) {
		//chest 63 slots. hotbar 0 id = 63-1-(8-0)=54
		//chest 63 slots. hotbar 4 id = 63-1-(8-4)=58
		if (index >= 9) return null;
		return slots[slots.length - 1 - (8 - index)];
	}

	public int getHotbarIndex(Slot slot) {
		for (int i = 0; i < 9; i++) {
			if (slot == getHotbarSlot(i)) {
				return i;
			}
		}
		return -1;
	}

	public int getOtherSlotsCount() {
		return slots.length - 36;
	}

	public Slot getOtherSlot(int index) {
		if (index >= getOtherSlotsCount()) return null;
		return slots[index];
	}

	public Slot getInventorySlot(int index) {
		//first 9 reversed slots are hotbar.
		if (index < 9) {
			return getHotbarSlot(index);
		}
		index -= 9;
		//chest 63 slots. modified slot 9 is upper left in player inv. 
		//9-9=0
		//new last = 63-9=54
		//54-27=27
		int last = slots.length - 9;
		int target = last - (27 - index);
		if (target >= last) return null;
		return slots[target];
	}

	public Iterator<Slot> getInventoryIterator() {
		return new IndexIterator<>(36, this::getInventorySlot);
	}

	public Iterator<Slot> getHotbarIterator() {
		return new IndexIterator<>(9, this::getHotbarSlot);
	}

	public Iterator<Slot> getOtherIterator() {
		return new IndexIterator<>(getOtherSlotsCount(), this::getOtherSlot);
	}

	public Iterator<Slot> getIterator() {
		return new IndexIterator<>(slots.length, i -> slots[i]);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass() + "\n");
		sb.append(this.windowId + " " + this.windowType + " " + Registries.registriesFromIds.get("minecraft:menu").get(windowType) + "\n");
		for (int i = 0; i < getOtherSlotsCount(); i++) {
			sb.append(" " + getOtherSlot(i).toStringShort());
			if (i % 9 == 8) sb.append("\n");
		}
		sb.append("\n");
		for (int i = 0; i < 36; i++) {
			sb.append(" " + getInventorySlot(i).toStringShort());
			if (i % 9 == 8) sb.append("\n");
		}
		sb.append("\nCursor: " + cursor.toStringShort());
		return sb.toString();
	}

}
