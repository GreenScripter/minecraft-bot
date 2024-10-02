package greenscripter.minecraft.packet.s2c.play;

import java.io.IOException;
import java.util.*;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.play.statistics.*;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class AwardStatsPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:award_stats");

	public List<StatisticsEntry> changed = new ArrayList<>();

	public AwardStatsPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(changed.size());
		for (StatisticsEntry entry: changed) {
			out.writeVarInt(entry.category().ordinal());
			out.writeVarInt(entry.statistic());
			out.writeVarInt(entry.value());
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		changed.clear();
		int entries = in.readVarInt();
		for (int i = 0; i < entries; i++) {
			changed.add(new StatisticsEntry(StatisticsCategory.values()[in.readVarInt()], in.readVarInt(), in.readVarInt()));
		}
	}
}
