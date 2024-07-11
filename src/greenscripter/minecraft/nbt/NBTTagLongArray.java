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

	public NBTTagLongArray copy() {
		NBTTagLongArray copy = new NBTTagLongArray();
		if (value != null) {
			copy.value = new long[value.length];
			System.arraycopy(value, 0, copy.value, 0, value.length);
		}
		return copy;
	}

	public String toString() {
		return Arrays.toString(value);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(value);
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NBTTagLongArray other = (NBTTagLongArray) obj;
		return Arrays.equals(value, other.value);
	}
}
