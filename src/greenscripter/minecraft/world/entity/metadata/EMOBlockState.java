package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMOBlockState extends EntityMetadata {

	public int valuePlusOne;

	public int id() {
		return 15;
	}

	public void read(MCInputStream in) throws IOException {
		valuePlusOne = in.readVarInt();
	}

}
