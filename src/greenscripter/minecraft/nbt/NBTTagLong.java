package greenscripter.minecraft.nbt;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

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

	public String toString() {
		return value + "l";
	}
}
