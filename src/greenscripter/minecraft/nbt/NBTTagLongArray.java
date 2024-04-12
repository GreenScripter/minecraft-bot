package greenscripter.minecraft.nbt;

import java.util.Arrays;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagLongArray extends NBTComponent {

	public long[] value;

	public byte getType() {
		return TAG_Long_Array;
	}

	public NBTComponent read(MCInputStream in) throws IOException {
		value = new long[in.readInt()];
		for (int i = 0; i < value.length; i++) {
			value[i] = in.readLong();
		}
		return this;
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeInt(value.length);
		for (int i = 0; i < value.length; i++) {
			out.writeLong(value[i]);
		}
	}

	public String toString() {
		return Arrays.toString(value);
	}
}
