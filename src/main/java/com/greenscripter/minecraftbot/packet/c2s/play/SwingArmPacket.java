package com.greenscripter.minecraftbot.packet.c2s.play;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class SwingArmPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:swing");

	public int hand;

	public SwingArmPacket() {}

	public SwingArmPacket(int hand) {
		this.hand = hand;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(hand);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		hand = in.readVarInt();
	}

	public static final int MAIN_HAND = 0;
	public static final int OFF_HAND = 1;
}
