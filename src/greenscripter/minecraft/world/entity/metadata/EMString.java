package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMString extends EntityMetadata {

	public String value;

	public int id() {
		return 4;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readString();
	}

}
