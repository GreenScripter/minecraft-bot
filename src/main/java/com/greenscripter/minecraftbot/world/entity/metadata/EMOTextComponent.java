package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.nbt.NBTComponent;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

public class EMOTextComponent extends EntityMetadata {

	public NBTComponent value;

	public int id() {
		return 6;
	}

	public void read(MCInputStream in) throws IOException {
		if (in.readBoolean()) value = in.readNBT();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeBoolean(value != null);
		if (value != null) out.writeNBT(value);
	}

	public String toString() {
		return "EMOTextComponent [" + (value != null ? "value=" + value : "") + "]";
	}

}
