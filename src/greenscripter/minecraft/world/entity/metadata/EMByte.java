package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMByte extends EntityMetadata {

	public byte value;

	public int id() {
		return 0;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readByte();
	}

}
