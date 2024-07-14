package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class BaseColorComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:base_color");

	public int dyeColor;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(dyeColor);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		dyeColor = in.readVarInt();
	}

	public BaseColorComponent copy() {
		BaseColorComponent c = new BaseColorComponent();
		c.dyeColor = dyeColor;
		return c;
	}

}
