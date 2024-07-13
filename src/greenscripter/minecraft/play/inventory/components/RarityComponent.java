package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class RarityComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:rarity");

	public int rarity;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(rarity);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		rarity = in.readVarInt();
	}

	public RarityComponent copy() {
		RarityComponent c = new RarityComponent();
		c.rarity = rarity;
		return c;
	}

	public static final int COMMON = 0;
	public static final int UNCOMMON = 1;
	public static final int RARE = 2;
	public static final int EPIC = 3;

}
