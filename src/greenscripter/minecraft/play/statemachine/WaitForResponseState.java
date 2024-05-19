package greenscripter.minecraft.play.statemachine;

import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.ClientStatusPacket;
import greenscripter.minecraft.packet.s2c.play.AwardStatsPacket;
import greenscripter.minecraft.play.handler.PlayHandler;

public class WaitForResponseState extends PlayerState {

	public boolean done = false;

	public WaitForResponseState() {
		until(e -> done);
		onInit(e -> {
			e.value.sendPacket(new ClientStatusPacket(ClientStatusPacket.STATS));
		});
		addStickyGameHandler(new PlayHandler() {

			int statsId = new AwardStatsPacket().id();

			public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
				if (p.id == statsId) {
					done = true;
				}
			}

			public List<Integer> handlesPackets() {
				return List.of(statsId);
			}
		});
	}

}