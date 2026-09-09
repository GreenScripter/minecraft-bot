package com.greenscripter.minecraftbot.play.handler;

import java.util.List;

import java.io.IOException;

import com.greenscripter.minecraftbot.ServerConnection;
import com.greenscripter.minecraftbot.packet.UnknownPacket;
import com.greenscripter.minecraftbot.packet.c2s.play.TeleportConfirmPacket;
import com.greenscripter.minecraftbot.packet.s2c.play.self.TeleportRequestPacket;
import com.greenscripter.minecraftbot.play.data.PositionData;

public class TeleportRequestPlayHandler extends PlayHandler {

	static int teleportPacketId = new TeleportRequestPacket().id();

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		if (p.id == teleportPacketId) {
			PositionData pos = sc.getData(PositionData.class);
			TeleportRequestPacket req = p.convert(new TeleportRequestPacket());
			req.makeNotRelative(pos.pos.x, pos.pos.y, pos.pos.z, pos.pitch, pos.yaw);
			pos.pos.x = req.x;
			pos.pos.y = req.y;
			pos.pos.z = req.z;
			pos.pitch = req.pitch;
			pos.yaw = req.yaw;
			sc.sendPacket(new TeleportConfirmPacket(req.teleportID));
		}
	}

	public List<Integer> handlesPackets() {
		return List.of(teleportPacketId);
	}
}
