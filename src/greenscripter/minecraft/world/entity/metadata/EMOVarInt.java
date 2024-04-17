package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMOVarInt extends EntityMetadata {

	public int valuePlusOne;

	public int id() {
		return 19;
	}

	public void read(MCInputStream in) throws IOException {
		valuePlusOne = in.readVarInt();
	}

}
