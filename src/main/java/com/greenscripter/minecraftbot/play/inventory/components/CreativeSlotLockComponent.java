package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class CreativeSlotLockComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:creative_slot_lock");

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {}

	public void fromBytes(MCInputStream in) throws IOException {}

	public CreativeSlotLockComponent copy() {
		CreativeSlotLockComponent c = new CreativeSlotLockComponent();
		return c;
	}

}
