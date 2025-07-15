package greenscripter.minecraft.play.statemachine;

import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.ClientPingPacket;
import greenscripter.minecraft.packet.s2c.play.ServerPongPacket;

public class WaitForPongState extends WaitForPacketState {

	public long ping;

	public WaitForPongState() {
		this(System.nanoTime());
		this.timeout = 2000;
	}

	public WaitForPongState(long ping) {
		super(p -> checkPong(p, ping), ServerPongPacket.packetId);
		this.ping = ping;

		onInit(e -> {
			e.value.sendPacket(new ClientPingPacket(ping));
		});
	}

	private static boolean checkPong(UnknownPacket p, long ping) {
		ServerPongPacket pong = p.convert(new ServerPongPacket());
		return ping == pong.value;
	}

}
