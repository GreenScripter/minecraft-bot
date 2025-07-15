package greenscripter.minecraft.play.statemachine;

import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.ClientPingPacket;
import greenscripter.minecraft.packet.s2c.play.ServerPongPacket;
import greenscripter.minecraft.play.data.PingIDData;

public class WaitForResponseState extends WaitForPacketState {

	private long id = 0;

	public WaitForResponseState() {
		super(ServerPongPacket.packetId);
		this.check = this::checkPong;

		onInit(e -> {
			id = e.value.getData(PingIDData.class).nextID();
			e.value.sendPacket(new ClientPingPacket(id));
		});
	}

	private boolean checkPong(UnknownPacket p) {
		ServerPongPacket pong = p.convert(new ServerPongPacket());
		return id == pong.value;
	}

}
