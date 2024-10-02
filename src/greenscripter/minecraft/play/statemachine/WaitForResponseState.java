package greenscripter.minecraft.play.statemachine;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.ClientPingPacket;
import greenscripter.minecraft.packet.s2c.play.ServerPongPacket;
import greenscripter.minecraft.play.data.PingIDData;
import greenscripter.minecraft.play.handler.PlayHandler;

import java.io.*;
import java.util.*;

public class WaitForResponseState extends PlayerState {

	private long id = 0;
	private boolean done = false;

	public WaitForResponseState() {
		until(e -> done);

		addStickyGameHandler(new PlayHandler() {
			public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
				if (p.id == ServerPongPacket.packetId) {
					var pong = p.convert(new ServerPongPacket());
					if (pong.value == id) {
						done = true;
					}
				}
			}

			public List<Integer> handlesPackets() {
				return List.of(ServerPongPacket.packetId);
			}
		});

		onInit(e -> {
			id = e.value.getData(PingIDData.class).nextID();
			e.value.sendPacket(new ClientPingPacket(id));
		});
	}

}