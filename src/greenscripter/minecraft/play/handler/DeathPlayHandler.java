package greenscripter.minecraft.play.handler;

import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.ClientStatusPacket;
import greenscripter.minecraft.packet.s2c.play.self.DeathPacket;
import greenscripter.minecraft.packet.s2c.play.self.SetHealthPacket;

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
