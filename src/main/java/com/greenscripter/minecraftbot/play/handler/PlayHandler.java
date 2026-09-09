package com.greenscripter.minecraftbot.play.handler;

import java.util.List;

import java.io.IOException;

import com.greenscripter.minecraftbot.ServerConnection;
import com.greenscripter.minecraftbot.packet.UnknownPacket;

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

	public void handleDisconnect(ServerConnection sc) throws IOException {

	}

}
