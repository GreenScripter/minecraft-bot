package greenscripter.minecraft.packet.s2c.play;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.play.statistics.StatisticsCategory;
import greenscripter.minecraft.play.statistics.StatisticsEntry;
import greenscripter.minecraft.play.statistics.StatisticsKey;
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
