package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ItemNameComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:custom_name");

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
