package greenscripter.minecraft.nbt;

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

	public String toString() {
		return value + "f";
	}
}
