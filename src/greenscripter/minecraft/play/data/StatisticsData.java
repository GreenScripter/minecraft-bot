package greenscripter.minecraft.play.data;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.s2c.play.AwardStatsPacket;
import greenscripter.minecraft.play.statistics.StatisticsKey;

import java.util.HashMap;

public class StatisticsData implements PlayData {
	private HashMap<StatisticsKey, Integer> stats = new HashMap<>();

	public void init(ServerConnection sc) {
		stats.clear();
	}

	public void handleStatsPacket(AwardStatsPacket packet) {
		packet.changed.forEach(chg -> {
			stats.put(chg.key(), chg.value());
		});
	}

	public int getStat(StatisticsKey key) {
		return stats.getOrDefault(key, 0);
	}
}
