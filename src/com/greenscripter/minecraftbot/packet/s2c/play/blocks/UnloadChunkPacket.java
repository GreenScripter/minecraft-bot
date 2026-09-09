package com.greenscripter.minecraftbot.packet.s2c.play.blocks;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class UnloadChunkPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:forget_level_chunk");

	public int x;
	public int z;

	public UnloadChunkPacket() {}

	public UnloadChunkPacket(int chunkX, int chunkZ) {
		x = chunkX;
		z = chunkZ;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeInt(z);
		out.writeInt(x);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		z = in.readInt();
		x = in.readInt();
	}

}
