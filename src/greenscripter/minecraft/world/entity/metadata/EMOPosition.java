package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMOPosition extends EntityMetadata {

	public Position value;

	public int id() {
		return 11;
	}

	public void read(MCInputStream in) throws IOException {
		if (in.readBoolean()) value = in.readPosition();
	}

}
