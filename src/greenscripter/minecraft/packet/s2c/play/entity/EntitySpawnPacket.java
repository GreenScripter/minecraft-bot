package greenscripter.minecraft.packet.s2c.play.entity;

import java.util.UUID;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class EntitySpawnPacket extends Packet {

	public int entityID;
	public UUID uuid;
	public int type;
	public double x;
	public double y;
	public double z;
	public byte pitch;
	public byte yaw;
	public byte headYaw;
	public int data;
	public short velocityX;
	public short velocityY;
	public short velocityZ;

	public EntitySpawnPacket() {}

	public int id() {
		return 0x01;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		uuid = in.readUUID();
		type = in.readVarInt();
		x = in.readDouble();
		y = in.readDouble();
		z = in.readDouble();
		pitch = in.readByte();
		yaw = in.readByte();
		headYaw = in.readByte();
		data = in.readVarInt();
		velocityX = in.readShort();
		velocityY = in.readShort();
		velocityZ = in.readShort();
	}

}
