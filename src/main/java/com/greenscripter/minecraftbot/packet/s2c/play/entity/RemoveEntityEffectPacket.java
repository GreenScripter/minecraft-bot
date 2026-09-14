package com.greenscripter.minecraftbot.packet.s2c.play.entity;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class RemoveEntityEffectPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:remove_mob_effect");

	public int entityId;
	public int effectId;

	public RemoveEntityEffectPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityId = in.readVarInt();
		effectId = in.readVarInt();
	}

}
