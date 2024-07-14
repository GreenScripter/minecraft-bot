package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class MaxDamageComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:max_damage");

	public int maxDamage;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(maxDamage);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		maxDamage = in.readVarInt();
	}

	public MaxDamageComponent copy() {
		MaxDamageComponent c = new MaxDamageComponent();
		c.maxDamage = maxDamage;
		return c;
	}

}
