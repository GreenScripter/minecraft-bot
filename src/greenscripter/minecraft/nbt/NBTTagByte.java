package greenscripter.minecraft.nbt;

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

	public String toString() {
		return value + "b";
	}

}
