package greenscripter.minecraft.nbt;

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

	public String toString() {
		return "\"" + value + "\"";
	}
}
