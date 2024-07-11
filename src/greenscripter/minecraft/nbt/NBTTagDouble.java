package greenscripter.minecraft.nbt;

import java.util.Objects;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagDouble extends NBTComponent {

	public double value;

	public byte getType() {
		return TAG_Double;
	}

	public NBTComponent read(MCInputStream in) throws IOException {
		value = in.readDouble();
		return this;
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeDouble(value);
	}

	public NBTTagDouble copy() {
		NBTTagDouble copy = new NBTTagDouble();
		copy.value = value;
		return copy;
	}

	public String toString() {
		return value + "d";
	}

	public int hashCode() {
		return Objects.hash(value);
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NBTTagDouble other = (NBTTagDouble) obj;
		return Double.doubleToLongBits(value) == Double.doubleToLongBits(other.value);
	}
}
