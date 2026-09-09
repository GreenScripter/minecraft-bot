package com.greenscripter.minecraftbot.nbt;

import java.util.Objects;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class NBTTagShort extends NBTComponent {

	public short value;

	public byte getType() {
		return TAG_Short;
	}

	public NBTComponent read(MCInputStream in) throws IOException {
		value = in.readShort();
		return this;
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeShort(value);
	}

	public NBTTagShort copy() {
		NBTTagShort copy = new NBTTagShort();
		copy.value = value;
		return copy;
	}

	public String toString() {
		return value + "s";
	}

	public int hashCode() {
		return Objects.hash(value);
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NBTTagShort other = (NBTTagShort) obj;
		return value == other.value;
	}

}
