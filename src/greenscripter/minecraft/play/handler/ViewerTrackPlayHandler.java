package greenscripter.minecraft.play.handler;

import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.AsyncSwarmController;
import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.s2c.play.self.LoginPlayPacket;
import greenscripter.minecraft.play.data.PlayData;

public class ViewerTrackPlayHandler extends PlayHandler {

	public AsyncSwarmController controller;

	public ViewerTrackPlayHandler() {
		if (!PlayData.playData.containsKey(ViewerTrackPlayData.class)) {
			PlayData.playData.put(ViewerTrackPlayData.class, ViewerTrackPlayData::new);
		}
	}

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		if (p.id == LoginPlayPacket.packetId) {
			ViewerTrackPlayData data = sc.getData(ViewerTrackPlayData.class);
			data.controller = controller;
			LoginPlayPacket loginPacket = p.convert(new LoginPlayPacket());
//			loginPacket.enableRespawnScreen = false;
			data.loginPacket = loginPacket;
		}
	}

	public List<Integer> handlesPackets() {
		return List.of(LoginPlayPacket.packetId);
	}

	static class ViewerTrackPlayData implements PlayData {

		LoginPlayPacket loginPacket;
		AsyncSwarmController controller;

	}
}
