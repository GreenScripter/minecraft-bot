package greenscripter.minecraft.nbt;

import java.util.Arrays;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagByteArray extends NBTComponent {

	public byte[] data;

	public byte getType() {
		return TAG_Byte_Array;
	}

	public NBTComponent read(MCInputStream in) throws IOException {
		data = new byte[in.readInt()];
		in.readFully(data);
		return this;
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeInt(data.length);
		out.write(data);
	}
	public String toString() {
		return Arrays.toString(data);
	}
}
