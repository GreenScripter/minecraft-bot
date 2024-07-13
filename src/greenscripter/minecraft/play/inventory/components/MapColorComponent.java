package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class MapColorComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:map_color");

	public int color;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeInt(color);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		color = in.readInt();
	}

	public MapColorComponent copy() {
		MapColorComponent c = new MapColorComponent();
		c.color = color;
		return c;
	}

}
