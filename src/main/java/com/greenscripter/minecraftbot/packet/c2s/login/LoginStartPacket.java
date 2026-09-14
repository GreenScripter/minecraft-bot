package com.greenscripter.minecraftbot.packet.c2s.login;

import java.util.UUID;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class LoginStartPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPacketId("login", "minecraft:hello");

	public String name;
	public UUID uuid;

	public LoginStartPacket() {}

	public LoginStartPacket(String name, UUID uuid) {
		this.name = name;
		this.uuid = uuid;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeString(name);
		out.writeUUID(uuid);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		name = in.readString();
		uuid = in.readUUID();
	}

}
