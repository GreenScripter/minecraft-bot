package greenscripter.minecraft.play.handler;

import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;

public abstract class PlayHandler {

	public void handlePacket(UnknownPacket packet, ServerConnection sc) throws IOException {

	}

	public void tick(ServerConnection sc) throws IOException {

	}

	public boolean handlesTick() {
		return false;
	}

	public List<Integer> handlesPackets() {
		return List.of();
	}

}
