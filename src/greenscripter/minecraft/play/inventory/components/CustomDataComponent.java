package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class CustomDataComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:custom_data");

	public NBTComponent data;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeNBT(data);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		data = in.readNBT();
	}

	public CustomDataComponent copy() {
		CustomDataComponent c = new CustomDataComponent();
		if (data != null) {
			c.data = data.copy();
		}
		return c;
	}

}
