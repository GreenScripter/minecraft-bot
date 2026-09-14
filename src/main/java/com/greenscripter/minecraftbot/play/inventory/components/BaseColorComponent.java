package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class BaseColorComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:base_color");

	public int dyeColor;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(dyeColor);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		dyeColor = in.readVarInt();
	}

	public BaseColorComponent copy() {
		BaseColorComponent c = new BaseColorComponent();
		c.dyeColor = dyeColor;
		return c;
	}

}
