package greenscripter.minecraft.play.handler;

import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.s2c.play.DisconnectPacket;

public class DisconnectHandler extends PlayHandler {

	int disconnectId = new DisconnectPacket().id();

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		if (p.id == disconnectId) {
			DisconnectPacket packet = p.convert(new DisconnectPacket());
			System.err.println("Disconnected for " + packet.reason.toString());
			sc.connectionState = ServerConnection.ConnectionState.DISCONNECTED;
			sc.socket.close();
		}
	}

	public List<Integer> handlesPackets() {
		return List.of(disconnectId);
	}
}
