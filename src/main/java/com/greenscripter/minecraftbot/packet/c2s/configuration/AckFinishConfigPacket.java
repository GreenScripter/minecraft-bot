package com.greenscripter.minecraftbot.packet.c2s.configuration;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class AckFinishConfigPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPacketId("configuration", "minecraft:finish_configuration");

	public AckFinishConfigPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
