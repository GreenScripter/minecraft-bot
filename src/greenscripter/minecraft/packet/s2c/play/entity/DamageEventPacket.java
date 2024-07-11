package greenscripter.minecraft.packet.s2c.play.entity;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class DamageEventPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:damage_event");

	public int entityID;
	public int damageTypeID;
	public int damagerIDPlusOne;
	public int directDamagerPlusOne;
	public boolean hasPos;

	public DamageEventPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		damageTypeID = in.readVarInt();
		damagerIDPlusOne = in.readVarInt();
		directDamagerPlusOne = in.readVarInt();
		hasPos = in.readBoolean();
	}

}
