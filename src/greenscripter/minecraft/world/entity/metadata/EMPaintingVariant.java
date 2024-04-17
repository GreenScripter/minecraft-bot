package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMPaintingVariant extends EntityMetadata {

	public int value;

	public int id() {
		return 24;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readVarInt();
	}

}
