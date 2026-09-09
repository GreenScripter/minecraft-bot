package com.greenscripter.minecraftbot.packet.c2s.configuration;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class PingReplyConfigPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPacketId("configuration", "minecraft:pong");

	public int value;

	public PingReplyConfigPacket() {}

	public PingReplyConfigPacket(int value) {
		this.value = value;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeInt(value);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		value = in.readInt();
	}

}
