package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class DamageComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:damage");

	public int damage;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(damage);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		damage = in.readVarInt();
	}

	public DamageComponent copy() {
		DamageComponent c = new DamageComponent();
		c.damage = damage;
		return c;
	}

}
