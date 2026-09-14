package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;
import com.greenscripter.minecraftbot.utils.play.Position;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

public class EMOGlobalPosition extends EntityMetadata {

	public String dimension;
	public Position pos;

	public int id() {
		return 25;
	}

	public void read(MCInputStream in) throws IOException {
		if (in.readBoolean()) {
			dimension = in.readString();
			pos = in.readPosition();
		}
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeBoolean(dimension != null);
		if (dimension != null) {
			out.writeString(dimension);
			out.writePosition(pos);
		}
	}

}
