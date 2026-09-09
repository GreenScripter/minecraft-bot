package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.nbt.NBTComponent;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class ItemNameComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:item_name");

	public NBTComponent itemName;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeNBT(itemName);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		itemName = in.readNBT();
	}

	public ItemNameComponent copy() {
		ItemNameComponent c = new ItemNameComponent();
		if (itemName != null) {
			c.itemName = itemName.copy();
		}
		return c;
	}

}
