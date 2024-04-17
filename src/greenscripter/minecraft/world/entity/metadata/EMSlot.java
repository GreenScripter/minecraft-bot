package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMSlot extends EntityMetadata {

	public Slot value;

	public int id() {
		return 7;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readSlot();
	}

}
