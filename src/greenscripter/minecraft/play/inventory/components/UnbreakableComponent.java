package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class UnbreakableComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:unbreakable");

	public boolean unbreakable;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeBoolean(unbreakable);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		unbreakable = in.readBoolean();
	}

	public UnbreakableComponent copy() {
		UnbreakableComponent c = new UnbreakableComponent();
		c.unbreakable = unbreakable;
		return c;
	}

}
