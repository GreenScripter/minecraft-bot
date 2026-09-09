package com.greenscripter.minecraftbot.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.play.inventory.Slot;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class ContainerComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:container");

	public List<Slot> slots = new ArrayList<>();

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(slots.size());
		for (Slot e : slots) {
			out.writeSlot(e);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			slots.add(in.readSlot());
		}
	}

	public ContainerComponent copy() {
		ContainerComponent c = new ContainerComponent();
		for (Slot e : slots) {
			c.slots.add(e);
		}
		return c;
	}

}
