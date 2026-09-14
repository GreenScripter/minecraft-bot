package com.greenscripter.minecraftbot.packet.c2s.login;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class LoginAcknowledgePacket extends Packet {

	public static final int packetId = PacketIds.getC2SPacketId("login", "minecraft:login_acknowledged");

	public LoginAcknowledgePacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {}

	public void fromBytes(MCInputStream in) throws IOException {}

}
