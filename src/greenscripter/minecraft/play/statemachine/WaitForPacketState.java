package greenscripter.minecraft.play.statemachine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.play.handler.PlayHandler;

public class WaitForPacketState extends PlayerState {

	public boolean done = false;

	public WaitForPacketState(int... ids) {
		this(null, ids);
	}

	public WaitForPacketState(Predicate<UnknownPacket> c, int... ids) {
		Predicate<UnknownPacket> check = c == null ? p -> true : c;
		List<Integer> packetIds = new ArrayList<>();
		for (int i : ids) {
			packetIds.add(i);
		}

		until(e -> done);
		addStickyGameHandler(new PlayHandler() {

			public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
				if (check.test(p)) {
					done = true;
				}
			}

			public List<Integer> handlesPackets() {
				return packetIds;
			}
		});
	}

}