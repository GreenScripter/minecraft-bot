package greenscripter.minecraft.packet.s2c.play.blocks;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class UnloadChunkPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:forget_level_chunk");

	public int x;
	public int z;

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		z = in.readInt();
		x = in.readInt();
	}

}
