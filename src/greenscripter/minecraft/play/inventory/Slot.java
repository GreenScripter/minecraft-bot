package greenscripter.minecraft.play.inventory;

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

}
