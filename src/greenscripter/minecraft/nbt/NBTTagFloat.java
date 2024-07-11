package greenscripter.minecraft.nbt;

import java.util.Objects;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagFloat extends NBTComponent {

	public float value;

	public byte getType() {
		return TAG_Float;
	}

	public NBTComponent read(MCInputStream in) throws IOException {
		value = in.readFloat();
		return this;
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeFloat(value);
	}

	public NBTTagFloat copy() {
		NBTTagFloat copy = new NBTTagFloat();
		copy.value = value;
		return copy;
	}

	public String toString() {
		return value + "f";
	}

	public int hashCode() {
		return Objects.hash(value);
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NBTTagFloat other = (NBTTagFloat) obj;
		return Float.floatToIntBits(value) == Float.floatToIntBits(other.value);
	}
}
