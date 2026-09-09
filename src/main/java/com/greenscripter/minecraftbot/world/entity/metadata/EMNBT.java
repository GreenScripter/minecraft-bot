package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.nbt.NBTComponent;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

public class EMNBT extends EntityMetadata {

	public NBTComponent value;

	public int id() {
		return 16;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readNBT();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeNBT(value);
	}

	public String toString() {
		return "EMNBT [" + (value != null ? "value=" + value : "") + "]";
	}
}
