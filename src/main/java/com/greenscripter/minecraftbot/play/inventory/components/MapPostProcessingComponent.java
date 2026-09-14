package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class MapPostProcessingComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:map_post_processing");

	public int type;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(type);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		type = in.readVarInt();
	}

	public MapPostProcessingComponent copy() {
		MapPostProcessingComponent c = new MapPostProcessingComponent();
		c.type = type;
		return c;
	}

	public static final int LOCK = 0;
	public static final int SCALE = 1;

}
