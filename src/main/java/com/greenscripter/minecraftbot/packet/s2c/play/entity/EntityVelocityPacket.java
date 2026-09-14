package com.greenscripter.minecraftbot.packet.s2c.play.entity;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class EntityVelocityPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:set_entity_motion");

	public int entityID;
	public short velocityX;
	public short velocityY;
	public short velocityZ;

	public EntityVelocityPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		velocityX = in.readShort();
		velocityY = in.readShort();
		velocityZ = in.readShort();
	}

}
