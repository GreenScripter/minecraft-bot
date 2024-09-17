package greenscripter.minecraft.play.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;

public class PlayPacketHandler extends PlayHandler {

	public BiConsumer<UnknownPacket, ServerConnection> onPacket;

	private List<Integer> packetTypes;

	public PlayPacketHandler(List<Integer> types) {
		packetTypes = Collections.unmodifiableList(new ArrayList<>(types));
	}

	public PlayPacketHandler(List<Integer> types, BiConsumer<UnknownPacket, ServerConnection> handler) {
		this(types);
		onPacket = handler;
	}

	public void handlePacket(UnknownPacket packet, ServerConnection sc) throws IOException {
		onPacket.accept(packet, sc);
	}

	public List<Integer> handlesPackets() {
		return packetTypes;
	}

}
