package greenscripter.minecraft.world.entity.metadata;

import java.util.UUID;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMOUUID extends EntityMetadata {

	public UUID value;

	public int id() {
		return 13;
	}

	public void read(MCInputStream in) throws IOException {
		if (in.readBoolean()) value = in.readUUID();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeBoolean(value != null);
		if (value != null) out.writeUUID(value);
	}

}
