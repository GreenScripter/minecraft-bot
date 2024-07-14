package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class AttributeModifiersComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:attribute_modifiers");

	public List<Attribute> attributes = new ArrayList<>();
	public boolean showInTooltip = true;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(attributes.size());
		for (Attribute e : attributes) {
			e.toBytes(out);
		}
		out.writeBoolean(showInTooltip);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			Attribute e = new Attribute();
			e.fromBytes(in);
			attributes.add(e);
		}
		showInTooltip = in.readBoolean();
	}

	public AttributeModifiersComponent copy() {
		AttributeModifiersComponent c = new AttributeModifiersComponent();
		c.showInTooltip = showInTooltip;
		for (Attribute e : attributes) {
			c.attributes.add(e.copy());
		}
		return c;
	}

	public static class Attribute {

		public int typeId;
		public UUID uuid;
		public String name;
		public double value;
		public int operation;
		public int slot;

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeVarInt(typeId);
			out.writeUUID(uuid);
			out.writeString(name);
			out.writeDouble(value);
			out.writeVarInt(operation);
			out.writeVarInt(slot);
		}

		public void fromBytes(MCInputStream in) throws IOException {
			typeId = in.readVarInt();
			uuid = in.readUUID();
			name = in.readString();
			value = in.readDouble();
			operation = in.readVarInt();
			slot = in.readVarInt();
		}

		public Attribute copy() {
			Attribute e = new Attribute();
			e.typeId = typeId;
			e.uuid = uuid;
			e.name = name;
			e.value = value;
			e.operation = operation;
			e.slot = slot;
			return e;
		}

		public static final int OPERATION_ADD = 0;
		public static final int OPERATION_MULTIPLY = 1;
		public static final int OPERATION_MULTIPLY_BASE = 2;

		public static final int SLOT_ANY = 0;
		public static final int SLOT_MAIN_HAND = 1;
		public static final int SLOT_OFF_HAND = 2;
		public static final int SLOT_HAND = 3;
		public static final int SLOT_FEET = 4;
		public static final int SLOT_LEGS = 5;
		public static final int SLOT_CHEST = 6;
		public static final int SLOT_HEAD = 7;
		public static final int SLOT_ARMOR = 8;
		public static final int SLOT_BODY = 9;

	}

}
