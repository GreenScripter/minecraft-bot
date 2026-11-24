package com.greenscripter.minecraftbot.packet.c2s.play;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.play.data.PlayerData;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class PlayerMovePacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:move_player_status_only");

	public int flags = 0;

	public PlayerMovePacket() {}

	public PlayerMovePacket(PlayerData data) {
		this(data.pos.flags);
	}

	public PlayerMovePacket(int flags) {
		this.flags = flags;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeByte(flags);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		flags = in.readByte();
	}

}
