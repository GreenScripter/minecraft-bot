package greenscripter.minecraft.play.handler;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.BlockStates;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.AckChunksPacket;
import greenscripter.minecraft.packet.c2s.play.TeleportConfirmPacket;
import greenscripter.minecraft.packet.s2c.play.AwardStatsPacket;
import greenscripter.minecraft.packet.s2c.play.SetTimePacket;
import greenscripter.minecraft.packet.s2c.play.blocks.*;
import greenscripter.minecraft.packet.s2c.play.self.*;
import greenscripter.minecraft.play.data.*;
import greenscripter.minecraft.utils.DynamicRegistry.RegistryEntry;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.world.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
