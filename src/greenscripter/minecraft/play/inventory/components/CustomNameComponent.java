package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class CustomNameComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:custom_name");

	public NBTComponent name;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeNBT(name);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		name = in.readNBT();
	}

	public CustomNameComponent copy() {
		CustomNameComponent c = new CustomNameComponent();
		if (name != null) {
			c.name = name.copy();
		}
		return c;
	}

}
