package greenscripter.minecraft.world;

import greenscripter.minecraft.nbt.NBTTagCompound;
import greenscripter.minecraft.utils.Position;

public class BlockEntity {

	public Position pos;
	public int type;
	public NBTTagCompound data;

	public BlockEntity() {

	}

	public String toString() {
		return "BlockEntity [" + (pos != null ? "pos=" + pos + ", " : "") + "type=" + type + ", " + (data != null ? "data=" + data : "") + "]";
	}

}
