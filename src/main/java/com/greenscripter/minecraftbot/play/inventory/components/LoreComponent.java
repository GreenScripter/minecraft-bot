package com.greenscripter.minecraftbot.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.nbt.NBTComponent;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class LoreComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:lore");

	public List<NBTComponent> lines = new ArrayList<>();

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(lines.size());
		for (NBTComponent c : lines) {
			out.writeNBT(c);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		lines.clear();
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			lines.add(in.readNBT());
		}
	}

	public LoreComponent copy() {
		LoreComponent c = new LoreComponent();
		for (NBTComponent nbt : lines) {
			c.lines.add(nbt.copy());
		}
		return c;
	}

}
