package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.play.data.PlayerData;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PlayerMovePacket extends Packet {

	public boolean onGround = true;

	public PlayerMovePacket() {}

	public PlayerMovePacket(PlayerData data) {
		this(data.pos.onGround);
	}

	public PlayerMovePacket(boolean onGround) {
		this.onGround = onGround;
	}

	public int id() {
		return 0x1A;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeBoolean(onGround);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
