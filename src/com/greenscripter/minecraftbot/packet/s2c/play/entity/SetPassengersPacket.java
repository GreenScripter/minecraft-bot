package com.greenscripter.minecraftbot.packet.s2c.play.entity;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class SetPassengersPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:set_passengers");

	public int entityId;
	public int[] passengersIds;

	public SetPassengersPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityId = in.readVarInt();
		int length = in.readVarInt();
		passengersIds = new int[length];
		for (int i = 0; i < length; i++) {
			passengersIds[i] = in.readVarInt();
		}
	}

}
