package com.greenscripter.minecraftbot.packet.c2s.play;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class ClientInfoPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:client_information");

	public String locale = "en_US";
	public byte viewDistance = 0;
	public int chatMode = 0;
	public boolean chatColors = true;
	public byte skinParts = (byte) 0xFF;
	public int mainHand = 1;
	public boolean filtering = false;
	public boolean listing = false;

	public ClientInfoPacket() {}

	public ClientInfoPacket(int viewDistance) {
		this.viewDistance = (byte) viewDistance;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeString(locale);
		out.writeByte(viewDistance);
		out.writeVarInt(chatMode);
		out.writeBoolean(chatColors);
		out.writeByte(skinParts);
		out.writeVarInt(mainHand);
		out.writeBoolean(filtering);
		out.writeBoolean(listing);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
