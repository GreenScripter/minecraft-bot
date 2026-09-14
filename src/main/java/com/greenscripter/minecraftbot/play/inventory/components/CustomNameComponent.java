package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.nbt.NBTComponent;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class CustomNameComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:custom_name");

	public NBTComponent name;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeNBT(name);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		name = in.readNBT();
	}

	public CustomNameComponent copy() {
		CustomNameComponent c = new CustomNameComponent();
		if (name != null) {
			c.name = name.copy();
		}
		return c;
	}

}
