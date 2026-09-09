package com.greenscripter.minecraftbot.play.handler;

import java.util.List;

import java.io.IOException;

import com.greenscripter.minecraftbot.ServerConnection;
import com.greenscripter.minecraftbot.packet.UnknownPacket;
import com.greenscripter.minecraftbot.packet.s2c.play.AwardStatsPacket;
import com.greenscripter.minecraftbot.play.data.StatisticsData;

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
