package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMPosition extends EntityMetadata {

	public Position value;

	public int id() {
		return 10;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readPosition();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writePosition(value);
	}
}
