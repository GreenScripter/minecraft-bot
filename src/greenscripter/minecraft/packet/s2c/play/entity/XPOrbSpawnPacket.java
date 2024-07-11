package greenscripter.minecraft.packet.s2c.play.entity;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class XPOrbSpawnPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:add_experience_orb");

	public int entityID;
	public double x;
	public double y;
	public double z;
	public short count;

	public XPOrbSpawnPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		x = in.readDouble();
		y = in.readDouble();
		z = in.readDouble();
		count = in.readShort();
	}

}
