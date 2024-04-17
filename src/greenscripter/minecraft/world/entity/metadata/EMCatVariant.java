package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMCatVariant extends EntityMetadata {

	public int value;

	public int id() {
		return 21;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readVarInt();
	}

}
