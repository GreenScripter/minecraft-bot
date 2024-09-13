package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.play.data.PlayerData;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PlayerMovePacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:move_player_status_only");

	public boolean onGround = true;

	public PlayerMovePacket() {}

	public PlayerMovePacket(PlayerData data) {
		this(data.pos.onGround);
	}

	public PlayerMovePacket(boolean onGround) {
		this.onGround = onGround;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeBoolean(onGround);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		onGround = in.readBoolean();
	}

}
