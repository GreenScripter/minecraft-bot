package com.greenscripter.minecraftbot.packet.s2c.configuration;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class KeepAliveConfigPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPacketId("configuration", "minecraft:keep_alive");

	public long value;

	public KeepAliveConfigPacket() {}

	public KeepAliveConfigPacket(long value) {
		this.value = value;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeLong(value);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		value = in.readLong();
	}

}
