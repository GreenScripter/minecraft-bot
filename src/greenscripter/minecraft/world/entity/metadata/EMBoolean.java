package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMBoolean extends EntityMetadata {

	public boolean value;

	public int id() {
		return 8;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readBoolean();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeBoolean(value);
	}

}
