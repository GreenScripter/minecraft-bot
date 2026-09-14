package com.greenscripter.minecraftbot.world;

import com.greenscripter.minecraftbot.nbt.NBTTagCompound;
import com.greenscripter.minecraftbot.utils.play.Position;

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
