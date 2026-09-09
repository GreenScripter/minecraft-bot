package com.greenscripter.minecraftbot.play.handler;

import java.util.List;

import java.io.IOException;

import com.greenscripter.minecraftbot.ServerConnection;
import com.greenscripter.minecraftbot.packet.UnknownPacket;
import com.greenscripter.minecraftbot.packet.c2s.play.KeepAliveReplyPacket;
import com.greenscripter.minecraftbot.packet.s2c.play.KeepAlivePacket;

public class KeepAlivePlayHandler extends PlayHandler {

	private int keepAliveId = new KeepAlivePacket().id();

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		if (p.id == keepAliveId) {
			sc.sendPacket(new KeepAliveReplyPacket(p.convert(new KeepAlivePacket()).value));
		}
	}

	public List<Integer> handlesPackets() {
		return List.of(keepAliveId);
	}
}
