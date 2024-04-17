package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMRotations extends EntityMetadata {

	public float rotX;
	public float rotY;
	public float rotZ;

	public int id() {
		return 9;
	}

	public void read(MCInputStream in) throws IOException {
		rotX = in.readFloat();
		rotY = in.readFloat();
		rotZ = in.readFloat();
	}

}
