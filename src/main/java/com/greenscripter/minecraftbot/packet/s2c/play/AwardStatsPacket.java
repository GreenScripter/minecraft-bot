package com.greenscripter.minecraftbot.packet.s2c.play;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.play.statistics.StatisticsCategory;
import com.greenscripter.minecraftbot.play.statistics.StatisticsEntry;
import com.greenscripter.minecraftbot.play.statistics.StatisticsKey;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class AwardStatsPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:award_stats");

	public List<StatisticsEntry> changed = new ArrayList<>();

	public AwardStatsPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(changed.size());
		for (StatisticsEntry entry : changed) {
			out.writeVarInt(entry.key().category().ordinal());
			out.writeVarInt(entry.key().statistic());
			out.writeVarInt(entry.value());
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		changed.clear();
		int entries = in.readVarInt();
		for (int i = 0; i < entries; i++) {
			changed.add(new StatisticsEntry(new StatisticsKey(StatisticsCategory.values()[in.readVarInt()], in.readVarInt()), in.readVarInt()));
		}
	}
}
