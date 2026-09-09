package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

public class EMArmadilloState extends EntityMetadata {

	public int value;

	public int id() {
		return 28;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readVarInt();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeVarInt(value);
	}

}
