package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class DamageComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:damage");

	public int damage;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(damage);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		damage = in.readVarInt();
	}

	public DamageComponent copy() {
		DamageComponent c = new DamageComponent();
		c.damage = damage;
		return c;
	}

}
