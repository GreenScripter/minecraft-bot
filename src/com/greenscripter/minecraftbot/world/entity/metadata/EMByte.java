package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

public class EMByte extends EntityMetadata {

	public byte value;

	public int id() {
		return 0;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readByte();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeByte(value);
	}

	public String toString() {
		return "EMByte [value=" + value + "]";
	}

}
