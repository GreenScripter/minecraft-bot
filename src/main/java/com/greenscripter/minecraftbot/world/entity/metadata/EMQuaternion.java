package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

public class EMQuaternion extends EntityMetadata {

	public float x;
	public float y;
	public float z;
	public float w;

	public int id() {
		return 30;
	}

	public void read(MCInputStream in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		z = in.readFloat();
		w = in.readFloat();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(z);
		out.writeFloat(w);
	}

}
