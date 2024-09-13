package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMByte extends EntityMetadata {

	public byte value;

	public int id() {
		return 0;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readByte();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeByte(value);
	}

	public String toString() {
		return "EMByte [value=" + value + "]";
	}

}
