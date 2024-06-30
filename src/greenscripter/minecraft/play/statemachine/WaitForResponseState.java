package greenscripter.minecraft.play.statemachine;

import greenscripter.minecraft.packet.c2s.play.ClientStatusPacket;
import greenscripter.minecraft.packet.s2c.play.AwardStatsPacket;

public class WaitForResponseState extends WaitForPacketState {

	boolean sent = false;

	public WaitForResponseState() {
		super(new AwardStatsPacket().id());
		//		new Exception().printStackTrace();
		//		onInit(e -> {
		//			System.out.println(e.other + " started waiting");
		//		});
		//		onTick(e -> {
		//			if (!sent) {
		//				e.value.sendPacket(new ClientStatusPacket(ClientStatusPacket.STATS));
		//				sent = true;
		//			}
		//		});
		//		then(new WaitTicksState(20));

		onInit(e -> {
			e.value.sendPacket(new ClientStatusPacket(ClientStatusPacket.STATS));
		});
	}

}