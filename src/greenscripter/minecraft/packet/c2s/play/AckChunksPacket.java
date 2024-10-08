package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class AckChunksPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:chunk_batch_received");

	public float chunksPerTick = 64;

	public AckChunksPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeFloat(chunksPerTick);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
