package greenscripter.minecraft.nbt;

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

	public String toString() {
		return value + "d";
	}
}
