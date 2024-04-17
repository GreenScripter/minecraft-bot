package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMOGlobalPosition extends EntityMetadata {

	public String dimension;
	public Position pos;

	public int id() {
		return 23;
	}

	public void read(MCInputStream in) throws IOException {
		if (in.readBoolean()) {
			dimension = in.readString();
			pos = in.readPosition();
		}
	}

}
