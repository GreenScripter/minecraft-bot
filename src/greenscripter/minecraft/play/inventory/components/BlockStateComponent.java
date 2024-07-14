package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class BlockStateComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:block_state");

	public List<Property> properties = new ArrayList<>();

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(properties.size());
		for (Property e : properties) {
			e.toBytes(out);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			Property e = new Property();
			e.fromBytes(in);
			properties.add(e);
		}
	}

	public BlockStateComponent copy() {
		BlockStateComponent c = new BlockStateComponent();
		for (Property e : properties) {
			c.properties.add(e.copy());
		}
		return c;
	}

	public static class Property {

		public String name;
		public String value;

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeString(name);
			out.writeString(value);
		}

		public void fromBytes(MCInputStream in) throws IOException {
			name = in.readString();
			value = in.readString();
		}

		public Property copy() {
			Property c = new Property();
			c.name = name;
			c.value = value;
			return c;
		}
	}

}
