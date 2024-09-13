package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMVarLong extends EntityMetadata {

	public long value;

	public int id() {
		return 2;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readVarLong();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeVarLong(value);
	}
}
