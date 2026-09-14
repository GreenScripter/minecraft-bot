package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.nbt.NBTComponent;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class EntityDataComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:entity_data");

	public NBTComponent data;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeNBT(data);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		data = in.readNBT();
	}

	public EntityDataComponent copy() {
		EntityDataComponent c = new EntityDataComponent();
		if (data != null) {
			c.data = data.copy();
		}
		return c;
	}

}
