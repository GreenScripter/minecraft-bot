package greenscripter.minecraft.packet.s2c.play.entity;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class XPOrbSpawnPacket extends Packet {

	public int entityID;
	public double x;
	public double y;
	public double z;
	public short count;

	public XPOrbSpawnPacket() {}

	public int id() {
		return 0x02;
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
