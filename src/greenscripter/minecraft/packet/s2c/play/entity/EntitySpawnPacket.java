package greenscripter.minecraft.packet.s2c.play.entity;

import java.util.UUID;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.Entity;

public class EntitySpawnPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:add_entity");

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

	public EntitySpawnPacket(Entity e) {
		entityID = e.entityId;
		uuid = e.uuid;
		type = e.type;
		x = e.pos.x;
		y = e.pos.y;
		z = e.pos.z;
		pitch = (byte) (e.pitch * 256f / 360f);
		yaw = (byte) (e.yaw * 256f / 360f);
		headYaw = (byte) (e.headYaw * 256f / 360f);
		data = e.data;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(entityID);
		out.writeUUID(uuid);
		out.writeVarInt(type);
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(z);
		out.writeByte(pitch);
		out.writeByte(yaw);
		out.writeByte(headYaw);
		out.writeVarInt(data);
		out.writeShort(velocityX);
		out.writeShort(velocityY);
		out.writeShort(velocityZ);
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
