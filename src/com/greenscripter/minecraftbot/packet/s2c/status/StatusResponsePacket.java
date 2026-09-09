package com.greenscripter.minecraftbot.packet.s2c.status;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class StatusResponsePacket extends Packet {

	public static final int packetId = PacketIds.getS2CPacketId("status", "minecraft:status_response");

	public String value;

	public StatusResponsePacket() {}

	public StatusResponsePacket(String value) {
		this.value = value;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeString(value);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		value = in.readString();
	}

}
