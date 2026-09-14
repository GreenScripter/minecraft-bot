package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class MaxStackSizeComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:max_stack_size");

	public int maxStackSize;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(maxStackSize);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		maxStackSize = in.readVarInt();
	}

	public MaxStackSizeComponent copy() {
		MaxStackSizeComponent c = new MaxStackSizeComponent();
		c.maxStackSize = maxStackSize;
		return c;
	}

}
