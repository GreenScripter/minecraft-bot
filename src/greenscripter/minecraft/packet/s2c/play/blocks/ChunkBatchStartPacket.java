package greenscripter.minecraft.packet.s2c.play.blocks;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ChunkBatchStartPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:chunk_batch_start");

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
	}

	public void fromBytes(MCInputStream in) throws IOException {
	}

}
