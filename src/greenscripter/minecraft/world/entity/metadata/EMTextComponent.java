package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.nbt.NBTTagCompound;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMTextComponent extends EntityMetadata {

	public NBTTagCompound value;

	public int id() {
		return 5;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readNBT();
	}

}
