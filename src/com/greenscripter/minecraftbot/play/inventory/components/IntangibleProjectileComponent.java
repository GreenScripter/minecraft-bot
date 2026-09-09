package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.nbt.NBTComponent;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class IntangibleProjectileComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:intangible_projectile");

	public NBTComponent nbt;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeNBT(nbt);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		nbt = in.readNBT();
	}

	public IntangibleProjectileComponent copy() {
		IntangibleProjectileComponent c = new IntangibleProjectileComponent();
		if (c.nbt != null) c.nbt = nbt.copy();
		return c;
	}

}
