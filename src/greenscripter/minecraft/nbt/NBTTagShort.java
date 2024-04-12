package greenscripter.minecraft.nbt;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagShort extends NBTComponent {

	public short value;

	public byte getType() {
		return TAG_Short;
	}

	public NBTComponent read(MCInputStream in) throws IOException {
		value = in.readShort();
		return this;
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeShort(value);
	}

	public String toString() {
		return value + "s";
	}

}
