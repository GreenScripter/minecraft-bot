package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

public class EMString extends EntityMetadata {

	public String value;

	public int id() {
		return 4;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readString();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeString(value);
	}
}
