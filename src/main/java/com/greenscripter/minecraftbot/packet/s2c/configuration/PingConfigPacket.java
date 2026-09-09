package com.greenscripter.minecraftbot.packet.s2c.configuration;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class PingConfigPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPacketId("configuration", "minecraft:ping");

	public int value;

	public PingConfigPacket() {}

	public PingConfigPacket(int value) {
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
