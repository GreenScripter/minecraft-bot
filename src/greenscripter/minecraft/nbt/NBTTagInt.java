package greenscripter.minecraft.nbt;

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

	public String toString() {
		return value + "";
	}
}
