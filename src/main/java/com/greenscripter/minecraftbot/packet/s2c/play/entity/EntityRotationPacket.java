package com.greenscripter.minecraftbot.packet.s2c.play.entity;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class EntityRotationPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:move_entity_rot");

	public int entityID;
	public byte yaw;
	public byte pitch;
	public boolean onGround;

	public EntityRotationPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		yaw = in.readByte();
		pitch = in.readByte();
		onGround = in.readBoolean();
	}

}
