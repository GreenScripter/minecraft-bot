package greenscripter.minecraft.play.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;

public class StackedPlayHandler extends PlayHandler {

	public List<PlayHandler> handlers = new ArrayList<>();

	public StackedPlayHandler() {

	}

	public StackedPlayHandler(PlayHandler a, PlayHandler b) {
		if (a instanceof StackedPlayHandler s) {
			handlers.addAll(s.handlers);
		} else {
			handlers.add(a);
		}
		if (b instanceof StackedPlayHandler s) {
			handlers.addAll(s.handlers);
		} else {
			handlers.add(b);
		}
	}

	public boolean handlesTick() {
		return handlers.stream().anyMatch(p -> p.handlesTick());
	}

	public List<Integer> handlesPackets() {
		return handlers.stream().mapMulti((PlayHandler h, Consumer<Integer> c) -> h.handlesPackets().stream().forEach(c::accept)).distinct().toList();
	}

	public void handlePacket(UnknownPacket packet, ServerConnection sc) throws IOException {
		for (PlayHandler h : handlers) {
			h.handlePacket(packet, sc);
		}
	}

	public void tick(ServerConnection sc) throws IOException {
		for (PlayHandler h : handlers) {
			h.tick(sc);
		}
	}

}
