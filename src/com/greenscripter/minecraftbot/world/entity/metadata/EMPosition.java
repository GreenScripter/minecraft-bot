package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;
import com.greenscripter.minecraftbot.utils.Position;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

public class EMPosition extends EntityMetadata {

	public Position value;

	public int id() {
		return 10;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readPosition();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writePosition(value);
	}
}
