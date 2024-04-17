package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMQuaternion extends EntityMetadata {

	public float x;
	public float y;
	public float z;
	public float w;

	public int id() {
		return 27;
	}

	public void read(MCInputStream in) throws IOException {
		x = in.readFloat();
		y = in.readFloat();
		z = in.readFloat();
		w = in.readFloat();
	}

}
