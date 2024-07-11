package greenscripter.minecraft.nbt;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagEnd extends NBTComponent {

	public byte getType() {
		return TAG_End;
	}

	public NBTComponent read(MCInputStream in) {
		return this;
	}

	public void write(MCOutputStream out) {}

	public NBTTagEnd copy() {
		NBTTagEnd copy = new NBTTagEnd();
		return copy;
	}

	public int hashCode() {
		return 1234567;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		return true;
	}
}
