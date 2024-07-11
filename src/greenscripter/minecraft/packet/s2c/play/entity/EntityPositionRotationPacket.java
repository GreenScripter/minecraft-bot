package greenscripter.minecraft.packet.s2c.play.entity;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class EntityPositionRotationPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:move_entity_pos_rot");

	public int entityID;
	public short deltaX;
	public short deltaY;
	public short deltaZ;
	public byte yaw;
	public byte pitch;
	public boolean onGround;

	public EntityPositionRotationPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		deltaX = in.readShort();
		deltaY = in.readShort();
		deltaZ = in.readShort();
		yaw = in.readByte();
		pitch = in.readByte();
		onGround = in.readBoolean();
	}

}
