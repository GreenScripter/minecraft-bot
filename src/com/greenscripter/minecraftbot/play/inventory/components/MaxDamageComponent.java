package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class MaxDamageComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:max_damage");

	public int maxDamage;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(maxDamage);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		maxDamage = in.readVarInt();
	}

	public MaxDamageComponent copy() {
		MaxDamageComponent c = new MaxDamageComponent();
		c.maxDamage = maxDamage;
		return c;
	}

}
