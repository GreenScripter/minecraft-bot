package com.greenscripter.minecraftbot.packet.s2c.configuration;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class FinishConfigPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPacketId("configuration", "minecraft:finish_configuration");

	public FinishConfigPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {}

	public void fromBytes(MCInputStream in) throws IOException {}

}
