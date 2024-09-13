package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMNBT extends EntityMetadata {

	public NBTComponent value;

	public int id() {
		return 16;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readNBT();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeNBT(value);
	}

	public String toString() {
		return "EMNBT [" + (value != null ? "value=" + value : "") + "]";
	}
}
