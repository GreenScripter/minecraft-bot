package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PotDecorationsComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:pot_decorations");

	public List<Integer> decorationIds = new ArrayList<>();

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(decorationIds.size());
		for (Integer e : decorationIds) {
			out.writeVarInt(e);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			decorationIds.add(in.readVarInt());
		}
	}

	public PotDecorationsComponent copy() {
		PotDecorationsComponent c = new PotDecorationsComponent();
		for (Integer e : decorationIds) {
			c.decorationIds.add(e);
		}
		return c;
	}

}
