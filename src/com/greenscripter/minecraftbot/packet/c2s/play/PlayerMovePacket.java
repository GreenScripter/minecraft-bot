package com.greenscripter.minecraftbot.packet.c2s.play;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.play.data.PlayerData;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

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
