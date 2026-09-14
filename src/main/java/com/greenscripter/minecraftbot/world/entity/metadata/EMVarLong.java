package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

public class EMVarLong extends EntityMetadata {

	public long value;

	public int id() {
		return 2;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readVarLong();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeVarLong(value);
	}
}
