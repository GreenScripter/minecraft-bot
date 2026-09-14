package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;
import com.greenscripter.minecraftbot.utils.play.Position;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

public class EMOPosition extends EntityMetadata {

	public Position value;

	public int id() {
		return 11;
	}

	public void read(MCInputStream in) throws IOException {
		if (in.readBoolean()) value = in.readPosition();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeBoolean(value != null);
		if (value != null) out.writePosition(value);
	}

}
