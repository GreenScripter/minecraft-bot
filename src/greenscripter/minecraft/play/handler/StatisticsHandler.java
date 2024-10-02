package greenscripter.minecraft.play.handler;

import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.s2c.play.AwardStatsPacket;
import greenscripter.minecraft.play.data.StatisticsData;

public class StatisticsHandler extends PlayHandler {

	static int statsPacketId = AwardStatsPacket.packetId;

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		if (p.id == statsPacketId) {
			sc.getData(StatisticsData.class).handleStatsPacket(p.convert(new AwardStatsPacket()));
		}
	}

	public List<Integer> handlesPackets() {
		return List.of(statsPacketId);
	}
}
