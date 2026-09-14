package com.greenscripter.minecraftbot.packet.s2c.play;

import java.util.UUID;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class PlayerInfoRemovePacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:player_info_remove");

	public UUID[] uuids;

	public PlayerInfoRemovePacket() {}

	public PlayerInfoRemovePacket(UUID[] uuids) {
		this.uuids = uuids;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(uuids.length);
		for (UUID uuid : uuids) {
			out.writeUUID(uuid);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		uuids = new UUID[in.readVarInt()];
		for (int i = 0; i < uuids.length; i++) {
			uuids[i] = in.readUUID();
		}
	}

}
