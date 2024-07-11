package greenscripter.minecraft.nbt;

import java.util.Objects;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagString extends NBTComponent {

	public String value;

	public byte getType() {
		return TAG_String;
	}

	public NBTComponent read(MCInputStream in) throws IOException {
		value = in.readUTF();
		return this;
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeUTF(value);
	}

	public NBTTagString copy() {
		NBTTagString copy = new NBTTagString();
		copy.value = value;
		return copy;
	}

	public String toString() {
		return "\"" + value + "\"";
	}

	public int hashCode() {
		return Objects.hash(value);
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NBTTagString other = (NBTTagString) obj;
		return Objects.equals(value, other.value);
	}
}
