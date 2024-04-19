package greenscripter.minecraft.nbt;

import java.util.Arrays;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagIntArray extends NBTComponent {

	public int[] value;

	public byte getType() {
		return TAG_Int_Array;
	}

	public NBTComponent read(MCInputStream in) throws IOException {
		value = new int[in.readInt()];
		for (int i = 0; i < value.length; i++) {
			value[i] = in.readInt();
		}
		return this;
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeInt(value.length);
		for (int i = 0; i < value.length; i++) {
			out.writeInt(value[i]);
		}
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
		NBTTagIntArray other = (NBTTagIntArray) obj;
		return Arrays.equals(value, other.value);
	}
}
