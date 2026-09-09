package com.greenscripter.minecraftbot.packet.s2c.play.self;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class SetExperiencePacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:set_experience");

	public float progress;
	public int level;
	public int totalXP;

	public SetExperiencePacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		progress = in.readFloat();
		level = in.readVarInt();
		totalXP = in.readVarInt();
	}

}
