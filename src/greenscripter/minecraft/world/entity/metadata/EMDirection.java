package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMDirection extends EntityMetadata {

	public int value;//Direction class index

	public int id() {
		return 12;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readVarInt();
	}

}
