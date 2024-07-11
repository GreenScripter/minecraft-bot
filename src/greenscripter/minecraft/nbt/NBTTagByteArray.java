package greenscripter.minecraft.nbt;

import java.util.Arrays;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagByteArray extends NBTComponent {

	public byte[] data;

	public byte getType() {
		return TAG_Byte_Array;
	}

	public NBTComponent read(MCInputStream in) throws IOException {
		data = new byte[in.readInt()];
		in.readFully(data);
		return this;
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeInt(data.length);
		out.write(data);
	}

	public NBTTagByteArray copy() {
		NBTTagByteArray copy = new NBTTagByteArray();
		if (data != null) {
			copy.data = new byte[data.length];
			System.arraycopy(data, 0, copy.data, 0, data.length);
		}
		return copy;
	}

	public String toString() {
		return Arrays.toString(data);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NBTTagByteArray other = (NBTTagByteArray) obj;
		return Arrays.equals(data, other.data);
	}
}
