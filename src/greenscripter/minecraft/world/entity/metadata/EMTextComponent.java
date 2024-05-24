package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMTextComponent extends EntityMetadata {

	public NBTComponent value;

	public int id() {
		return 5;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readNBT();
	}

}
