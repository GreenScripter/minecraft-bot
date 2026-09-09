package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class FireResistantComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:fire_resistant");

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {}

	public void fromBytes(MCInputStream in) throws IOException {}

	public FireResistantComponent copy() {
		FireResistantComponent c = new FireResistantComponent();
		return c;
	}

}
