package greenscripter.minecraft.play.statemachine;

import greenscripter.minecraft.packet.c2s.play.ClientStatusPacket;
import greenscripter.minecraft.packet.s2c.play.AwardStatsPacket;

public class WaitForResponseState extends WaitForPacketState {

	public WaitForResponseState() {
		super(AwardStatsPacket.packetId);

		onInit(e -> {
			e.value.sendPacket(new ClientStatusPacket(ClientStatusPacket.STATS));
		});
	}

}