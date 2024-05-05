package greenscripter.minecraft.play.inventory;

import java.util.Objects;

import greenscripter.minecraft.gameinfo.Registries.ItemInfo;
import greenscripter.minecraft.nbt.NBTTagCompound;

public class Slot {

	public boolean present;
	public int itemId;
	public byte itemCount;
	public NBTTagCompound nbt;

	public String toString() {
		return "Slot [present=" + present + ", itemId=" + itemId + ", itemCount=" + itemCount + ", nbt=" + nbt + "]";
	}

	public void become(Slot other) {
		this.present = other.present;
		this.itemId = other.itemId;
		this.itemCount = other.itemCount;
		this.nbt = other.nbt;
	}

	public void add(int count) {
		itemCount += count;
		if (itemCount <= 0) {
			present = false;
			itemId = 0;
			itemCount = 0;
			nbt = null;
		}
	}

	public void setCount(int count) {
		itemCount = (byte) count;
		if (itemCount <= 0) {
			present = false;
			itemId = 0;
			itemCount = 0;
			nbt = null;
		}
	}

	public String getItemId() {
		if (!present) return null;
		return ItemId.itemRegistry.get(itemId);
	}

	public ItemInfo getItemInfo() {
		if (!present) return null;
		return ItemId.itemInfo.get(itemId);
	}

	public boolean equivalent(Slot other) {
		if (present == other.present && present == false) return true;
		return itemId == other.itemId && Objects.equals(nbt, other.nbt) && present == other.present;
	}
}
