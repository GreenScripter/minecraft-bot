package greenscripter.minecraft.packet.s2c.play.entity;

import java.util.UUID;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class EntityAttributesPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:update_attributes");

	public int entityID;
	public Attribute[] attributes;

	public EntityAttributesPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		attributes = new Attribute[in.readVarInt()];
		for (int i = 0; i < attributes.length; i++) {
			Attribute a = new Attribute();
			a.key = in.readString();
			a.value = in.readDouble();
			a.modifiers = new Modifier[in.readVarInt()];
			for (int j = 0; j < a.modifiers.length; j++) {
				Modifier m = new Modifier();
				m.id = in.readUUID();
				m.amount = in.readDouble();
				m.operation = in.readByte();
				a.modifiers[j] = m;
			}
			attributes[i] = a;
		}
	}

	public static class Attribute {

		public String key;
		public double value;
		public Modifier[] modifiers;
	}

	public static class Modifier {

		public UUID id;
		public double amount;
		public byte operation;
	}
}
