package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ProfileComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:profile");

	public String name;
	public UUID uuid;
	public List<Property> properties = new ArrayList<>();

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeBoolean(name != null);
		if (name != null) {
			out.writeString(name);
		}
		out.writeBoolean(uuid != null);
		if (uuid != null) {
			out.writeUUID(uuid);
		}
		out.writeVarInt(properties.size());
		for (Property e : properties) {
			e.toBytes(out);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		if (in.readBoolean()) {
			name = in.readString();
		}
		if (in.readBoolean()) {
			uuid = in.readUUID();
		}
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			Property e = new Property();
			e.fromBytes(in);
			properties.add(e);
		}
	}

	public ProfileComponent copy() {
		ProfileComponent c = new ProfileComponent();
		c.uuid = uuid;
		c.name = name;
		for (Property e : properties) {
			c.properties.add(e.copy());
		}
		return c;
	}

	public static class Property {

		public String name;
		public String value;
		public String signature;

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeString(name);
			out.writeString(value);
			out.writeBoolean(signature != null);
			if (signature != null) {
				out.writeString(signature);
			}
		}

		public void fromBytes(MCInputStream in) throws IOException {
			name = in.readString();
			value = in.readString();
			if (in.readBoolean()) {
				signature = in.readString();
			}
		}

		public Property copy() {
			Property c = new Property();
			c.name = name;
			c.signature = signature;
			c.value = value;
			return c;
		}
	}

}
