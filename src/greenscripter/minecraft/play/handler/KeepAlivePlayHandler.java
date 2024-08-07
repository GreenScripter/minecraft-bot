package greenscripter.minecraft.play.handler;

import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.KeepAliveReplyPacket;
import greenscripter.minecraft.packet.s2c.play.KeepAlivePacket;

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
