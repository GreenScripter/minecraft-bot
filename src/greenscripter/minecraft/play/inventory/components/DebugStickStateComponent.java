package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class DebugStickStateComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:debug_stick_state");

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

	public DebugStickStateComponent copy() {
		DebugStickStateComponent c = new DebugStickStateComponent();
		if (data != null) {
			c.data = data.copy();
		}
		return c;
	}

}
