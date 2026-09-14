package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class MapColorComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:map_color");

	public int color;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeInt(color);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		color = in.readInt();
	}

	public MapColorComponent copy() {
		MapColorComponent c = new MapColorComponent();
		c.color = color;
		return c;
	}

}
