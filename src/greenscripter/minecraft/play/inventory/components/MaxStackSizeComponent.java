package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class MaxStackSizeComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:max_stack_size");

	public int maxStackSize;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(maxStackSize);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		maxStackSize = in.readVarInt();
	}

	public MaxStackSizeComponent copy() {
		MaxStackSizeComponent c = new MaxStackSizeComponent();
		c.maxStackSize = maxStackSize;
		return c;
	}

}
