package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class IntangibleProjectileComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:intangible_projectile");

	public NBTComponent nbt;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeNBT(nbt);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		nbt = in.readNBT();
	}

	public IntangibleProjectileComponent copy() {
		IntangibleProjectileComponent c = new IntangibleProjectileComponent();
		c.nbt = nbt.copy();
		return c;
	}

}
