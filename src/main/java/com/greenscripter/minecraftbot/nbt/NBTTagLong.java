package com.greenscripter.minecraftbot.nbt;

import java.util.Objects;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class NBTTagLong extends NBTComponent {

	public long value;

	public byte getType() {
		return TAG_Long;
	}

	public NBTComponent read(MCInputStream in) throws IOException {
		value = in.readLong();
		return this;
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeLong(value);
	}

	public NBTTagLong copy() {
		NBTTagLong copy = new NBTTagLong();
		copy.value = value;
		return copy;
	}

	public String toString() {
		return value + "l";
	}

	public int hashCode() {
		return Objects.hash(value);
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NBTTagLong other = (NBTTagLong) obj;
		return value == other.value;
	}
}
