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

}
