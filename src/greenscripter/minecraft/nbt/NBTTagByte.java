
package greenscripter.minecraft.nbt;

import java.util.Objects;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagByte extends NBTComponent {

	public byte value;

	public byte getType() {
		return TAG_Byte;
	}

	public NBTComponent read(MCInputStream in) throws IOException {
		value = in.readByte();
		return this;
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeByte(value);
	}

	public NBTTagByte copy() {
		NBTTagByte copy = new NBTTagByte();
		copy.value = value;
		return copy;
	}

	public String toString() {
		return value + "b";
	}

	public int hashCode() {
		return Objects.hash(value);
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NBTTagByte other = (NBTTagByte) obj;
		return value == other.value;
	}

}
