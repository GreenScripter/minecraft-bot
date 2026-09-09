package com.greenscripter.minecraftbot.packet.s2c.play.entity;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class TeleportEntityPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:teleport_entity");

	public int entityID;
	public double x;
	public double y;
	public double z;
	public byte yaw;
	public byte pitch;
	public boolean onGround;

	public TeleportEntityPacket() {}

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
		yaw = in.readByte();
		pitch = in.readByte();
		onGround = in.readBoolean();
	}

}
