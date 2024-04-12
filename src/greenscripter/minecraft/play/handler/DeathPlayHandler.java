package greenscripter.minecraft.play.handler;

import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.ClientStatusPacket;
import greenscripter.minecraft.packet.s2c.play.DeathPacket;

public class DeathPlayHandler extends PlayHandler {

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		//		DeathPacket req = p.convert(new DeathPacket());
		//		System.out.println(req.message);
		sc.out.writePacket(new ClientStatusPacket());
	}

	public List<Integer> handlesPackets() {
		return List.of(new DeathPacket().id());
	}
}
