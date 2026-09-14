package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

public class EMDirection extends EntityMetadata {

	public int value;//Direction class index

	public int id() {
		return 12;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readVarInt();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeVarInt(value);
	}
}
