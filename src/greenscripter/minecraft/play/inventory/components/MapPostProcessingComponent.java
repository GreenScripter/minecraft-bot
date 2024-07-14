package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class MapPostProcessingComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:damage");

	public int type;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(type);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		type = in.readVarInt();
	}

	public MapPostProcessingComponent copy() {
		MapPostProcessingComponent c = new MapPostProcessingComponent();
		c.type = type;
		return c;
	}

	public static final int LOCK = 0;
	public static final int SCALE = 1;

}
