package com.greenscripter.minecraftbot.packet.s2c.play;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class SetTimePacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:set_time");

	public long worldAge;
	public long timeOfDay;

	public SetTimePacket() {}

	public SetTimePacket(long worldAge, long timeOfDay) {
		this.worldAge = worldAge;
		this.timeOfDay = timeOfDay;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeLong(worldAge);
		out.writeLong(timeOfDay);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		worldAge = in.readLong();
		timeOfDay = in.readLong();
	}
}
