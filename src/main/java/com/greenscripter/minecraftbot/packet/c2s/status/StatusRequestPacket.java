package com.greenscripter.minecraftbot.packet.c2s.status;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class StatusRequestPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPacketId("status", "minecraft:status_request");

	public StatusRequestPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {}

	public void fromBytes(MCInputStream in) throws IOException {}

}
