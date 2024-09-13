package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.play.data.PlayerData;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PlayerMoveRotationPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:move_player_rot");

	public float yaw;
	public float pitch;
	public boolean onGround = true;

	public PlayerMoveRotationPacket() {}

	public PlayerMoveRotationPacket(PlayerData data) {
		this(data.pos.yaw, data.pos.pitch);
	}

	public PlayerMoveRotationPacket(float yaw2, float pitch2) {
		this.yaw = yaw2;
		this.pitch = pitch2;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeFloat(yaw);
		out.writeFloat(pitch);
		out.writeBoolean(onGround);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		yaw = in.readFloat();
		pitch = in.readFloat();
		onGround = in.readBoolean();
	}

}
