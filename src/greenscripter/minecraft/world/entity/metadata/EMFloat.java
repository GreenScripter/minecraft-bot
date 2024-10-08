package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMFloat extends EntityMetadata {

	public float value;

	public int id() {
		return 3;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readFloat();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeFloat(value);
	}

	public String toString() {
		return "EMFloat [value=" + value + "]";
	}

}
