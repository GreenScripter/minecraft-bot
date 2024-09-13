package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMSlot extends EntityMetadata {

	public Slot value;

	public int id() {
		return 7;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readSlot();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeSlot(value);
	}
}
