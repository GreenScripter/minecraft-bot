package greenscripter.minecraft.packet.s2c.play.entity;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class TeleportEntityPacket extends Packet {

	public int entityID;
	public double x;
	public double y;
	public double z;
	public byte yaw;
	public byte pitch;
	public boolean onGround;

	public TeleportEntityPacket() {}

	public int id() {
		return 0x6D;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		x = in.readDouble();
		y = in.readDouble();
		z = in.readDouble();
		yaw = in.readByte();
		pitch = in.readByte();
		onGround = in.readBoolean();
	}

}
