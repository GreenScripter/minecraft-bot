package greenscripter.minecraft.nbt;

import java.util.Objects;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagInt extends NBTComponent {

	public int value;

	public byte getType() {
		return TAG_Int;
	}

	public NBTComponent read(MCInputStream in) throws IOException {
		value = in.readInt();
		return this;
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeInt(value);
	}

	public NBTTagInt copy() {
		NBTTagInt copy = new NBTTagInt();
		copy.value = value;
		return copy;
	}

	public String toString() {
		return value + "";
	}

	public int hashCode() {
		return Objects.hash(value);
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NBTTagInt other = (NBTTagInt) obj;
		return value == other.value;
	}
}
