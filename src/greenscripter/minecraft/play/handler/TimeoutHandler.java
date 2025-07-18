package greenscripter.minecraft.play.handler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.s2c.play.KeepAlivePacket;

public class TimeoutHandler extends PlayHandler {

	int pingId = new KeepAlivePacket().id();

	Map<ServerConnection, Long> times = new ConcurrentHashMap<>();

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		if (p.id == pingId) {
			times.put(sc, System.currentTimeMillis());
		}
	}

	public void tick(ServerConnection sc) throws IOException {
		if (times.containsKey(sc)) if (System.currentTimeMillis() - times.get(sc) > 30000) {
			times.remove(sc);
			throw new IOException("Minecraft Timeout");
		}
	}

	public void handleDisconnect(ServerConnection sc) throws IOException {
		times.remove(sc);
	}

	public List<Integer> handlesPackets() {
		return List.of(pingId);
	}

	public boolean handlesTick() {
		return true;
	}

}
