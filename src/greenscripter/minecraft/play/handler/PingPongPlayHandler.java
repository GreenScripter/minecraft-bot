package greenscripter.minecraft.play.handler;

import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.ClientPongPacket;
import greenscripter.minecraft.packet.s2c.play.ServerPingPacket;

public class PingPongPlayHandler extends PlayHandler {

	int pingId = new ServerPingPacket().id();

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		if (p.id == pingId) {
			//			System.out.println("Ponged "+p.convert(new PingPacket()).value);
			sc.sendPacket(new ClientPongPacket(p.convert(new ServerPingPacket()).value));
		}
	}

	public List<Integer> handlesPackets() {
		return List.of(pingId);
	}
}
