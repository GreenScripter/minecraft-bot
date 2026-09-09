package com.greenscripter.minecraftbot.packet.c2s.play;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.play.data.PlayerData;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

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
