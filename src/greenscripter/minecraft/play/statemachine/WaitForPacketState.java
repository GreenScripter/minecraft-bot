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
	public long start = System.currentTimeMillis();
	public long timeout = Long.MAX_VALUE;
	public Predicate<UnknownPacket> check;

	public WaitForPacketState(int... ids) {
		this(null, ids);
	}

	public WaitForPacketState(Predicate<UnknownPacket> c, int... ids) {
		check = c == null ? p -> true : c;
		List<Integer> packetIds = new ArrayList<>();
		for (int i : ids) {
			packetIds.add(i);
		}

		onInit(e -> start = System.currentTimeMillis());

		until(e -> done || (System.currentTimeMillis() - start > timeout));
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
