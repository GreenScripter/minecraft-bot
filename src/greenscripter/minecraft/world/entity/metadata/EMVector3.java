package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMVector3 extends EntityMetadata {

	public float x;
	public float y;
	public float z;

	public int id() {
		return 26;
	}

	public void read(MCInputStream in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		z = in.readFloat();
	}

}
