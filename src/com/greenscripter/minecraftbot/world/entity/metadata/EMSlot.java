package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.play.inventory.Slot;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

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
