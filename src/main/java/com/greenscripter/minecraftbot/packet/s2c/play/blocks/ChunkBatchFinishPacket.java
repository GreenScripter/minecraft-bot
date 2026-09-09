package com.greenscripter.minecraftbot.packet.s2c.play.blocks;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class ChunkBatchFinishPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:chunk_batch_finished");

	public int batchSize;

	public ChunkBatchFinishPacket() {

	}

	public ChunkBatchFinishPacket(int batchSize) {
		this.batchSize = batchSize;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(batchSize);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		batchSize = in.readVarInt();
	}

}
