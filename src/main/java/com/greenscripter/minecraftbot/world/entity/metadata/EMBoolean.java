package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

public class EMBoolean extends EntityMetadata {

	public boolean value;

	public int id() {
		return 8;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readBoolean();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeBoolean(value);
	}

}
