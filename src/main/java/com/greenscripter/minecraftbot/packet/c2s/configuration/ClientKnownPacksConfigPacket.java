package com.greenscripter.minecraftbot.packet.c2s.configuration;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class ClientKnownPacksConfigPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPacketId("configuration", "minecraft:select_known_packs");

	public String[] namespaces = {};
	public String[] ids = {};
	public String[] versions = {};

	public ClientKnownPacksConfigPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(namespaces.length);
		for (int i = 0; i < namespaces.length; i++) {
			out.writeString(namespaces[i]);
			out.writeString(ids[i]);
			out.writeString(versions[i]);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
