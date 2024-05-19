package greenscripter.minecraft.play.handler;

import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.PongPacket;
import greenscripter.minecraft.packet.s2c.play.PingPacket;

public class PingPongPlayHandler extends PlayHandler {

	int pingId = new PingPacket().id();

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		if (p.id == pingId) {
			//			System.out.println("Ponged "+p.convert(new PingPacket()).value);
			sc.sendPacket(new PongPacket(p.convert(new PingPacket()).value));
		}
	}

	public List<Integer> handlesPackets() {
		return List.of(pingId);
	}
}
