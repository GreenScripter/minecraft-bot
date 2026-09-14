package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

public class EMCatVariant extends EntityMetadata {

	public int value;

	public int id() {
		return 22;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readVarInt();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeVarInt(value);
	}
}
