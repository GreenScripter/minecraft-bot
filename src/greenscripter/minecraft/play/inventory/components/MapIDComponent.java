package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class MapIDComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:map_id");

	public int mapId;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(mapId);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		mapId = in.readVarInt();
	}

	public MapIDComponent copy() {
		MapIDComponent c = new MapIDComponent();
		c.mapId = mapId;
		return c;
	}

}
