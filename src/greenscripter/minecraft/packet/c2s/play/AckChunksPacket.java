package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class AckChunksPacket extends Packet {

	public float chunksPerTick = 64;

	public AckChunksPacket() {}

	public int id() {
		return 0x07;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeFloat(chunksPerTick);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
