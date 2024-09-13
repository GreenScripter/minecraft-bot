package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMOVarInt extends EntityMetadata {

	public int valuePlusOne;

	public int id() {
		return 20;
	}

	public void read(MCInputStream in) throws IOException {
		valuePlusOne = in.readVarInt();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeVarInt(valuePlusOne);
	}

	public String toString() {
		return "EMOVarInt [valuePlusOne=" + valuePlusOne + "]";
	}
}
