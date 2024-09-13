package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMOTextComponent extends EntityMetadata {

	public NBTComponent value;

	public int id() {
		return 6;
	}

	public void read(MCInputStream in) throws IOException {
		if (in.readBoolean()) value = in.readNBT();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeBoolean(value != null);
		if (value != null) out.writeNBT(value);
	}

	public String toString() {
		return "EMOTextComponent [" + (value != null ? "value=" + value : "") + "]";
	}

}
