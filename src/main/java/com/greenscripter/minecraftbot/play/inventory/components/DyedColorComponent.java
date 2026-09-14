package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class DyedColorComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:dyed_color");

	public int color;
	public boolean showInTooltip = true;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeInt(color);
		out.writeBoolean(showInTooltip);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		color = in.readInt();
		showInTooltip = in.readBoolean();
	}

	public DyedColorComponent copy() {
		DyedColorComponent c = new DyedColorComponent();
		c.color = color;
		c.showInTooltip = showInTooltip;
		return c;
	}

}
