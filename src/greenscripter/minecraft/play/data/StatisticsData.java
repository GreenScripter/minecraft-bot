package greenscripter.minecraft.play.data;

import java.util.HashMap;
import java.util.Map;

import greenscripter.minecraft.packet.s2c.play.AwardStatsPacket;
import greenscripter.minecraft.play.statistics.StatisticsKey;

public class StatisticsData implements PlayData {

	private Map<StatisticsKey, Integer> stats = new HashMap<>();

	public void handleStatsPacket(AwardStatsPacket packet) {
		packet.changed.forEach(chg -> {
			stats.put(chg.key(), chg.value());
		});
	}

	public int getStat(StatisticsKey key) {
		return stats.getOrDefault(key, 0);
	}
}
