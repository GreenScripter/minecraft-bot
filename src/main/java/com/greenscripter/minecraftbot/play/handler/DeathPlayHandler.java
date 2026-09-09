package com.greenscripter.minecraftbot.play.handler;

import java.util.List;

import java.io.IOException;

import com.greenscripter.minecraftbot.ServerConnection;
import com.greenscripter.minecraftbot.packet.UnknownPacket;
import com.greenscripter.minecraftbot.packet.c2s.play.ClientStatusPacket;
import com.greenscripter.minecraftbot.packet.s2c.play.self.DeathPacket;
import com.greenscripter.minecraftbot.packet.s2c.play.self.SetHealthPacket;

public class DeathPlayHandler extends PlayHandler {

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		if (p.id == DeathPacket.packetId) {
			sc.sendPacket(new ClientStatusPacket(ClientStatusPacket.RESPAWN));
		} else if (p.id == SetHealthPacket.packetId) {
			SetHealthPacket health = p.convert(new SetHealthPacket());
			if (health.health <= 0) {
				sc.sendPacket(new ClientStatusPacket(ClientStatusPacket.RESPAWN));
			}
		}
	}

	public List<Integer> handlesPackets() {
		return List.of(DeathPacket.packetId, SetHealthPacket.packetId);
	}
}
