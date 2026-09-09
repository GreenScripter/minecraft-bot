package com.greenscripter.minecraftbot.packet.s2c.play.blocks;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class ChunkBatchStartPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:chunk_batch_start");

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {}

	public void fromBytes(MCInputStream in) throws IOException {}

}
