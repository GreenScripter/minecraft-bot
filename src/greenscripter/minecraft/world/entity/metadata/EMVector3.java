package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMVector3 extends EntityMetadata {

	public float x;
	public float y;
	public float z;

	public int id() {
		return 29;
	}

	public void read(MCInputStream in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		z = in.readFloat();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeFloat(x);
		out.writeFloat(y);
		out.writeFloat(z);
	}

}
