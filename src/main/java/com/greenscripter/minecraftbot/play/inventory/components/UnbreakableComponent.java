package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class UnbreakableComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:unbreakable");

	public boolean unbreakable;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeBoolean(unbreakable);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		unbreakable = in.readBoolean();
	}

	public UnbreakableComponent copy() {
		UnbreakableComponent c = new UnbreakableComponent();
		c.unbreakable = unbreakable;
		return c;
	}

}
