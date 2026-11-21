package com.greenscripter.minecraftbot.packet.s2c.play;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class SetTimePacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:set_time");

	public long worldAge;
	public Clock[] clocks;

	public SetTimePacket() {}

	public SetTimePacket(long worldAge) {
		this.worldAge = worldAge;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeLong(worldAge);
		out.writeVarInt(clocks.length);
		for (Clock c : clocks) {
			out.writeVarInt(c.id);
			out.writeVarLong(c.time);
			out.writeFloat(c.fraction);
			out.writeFloat(c.rate);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		worldAge = in.readLong();
		clocks = new Clock[in.readVarInt()];
		for (int i = 0; i < clocks.length; i++) {
			var c = clocks[i] = new Clock();
			c.id = in.readVarInt();
			c.time = in.readVarLong();
			c.fraction = in.readFloat();
			c.rate = in.readFloat();
		}
	}

	public static class Clock {

		public int id;
		public long time;
		public float fraction;
		public float rate;
	}
}
