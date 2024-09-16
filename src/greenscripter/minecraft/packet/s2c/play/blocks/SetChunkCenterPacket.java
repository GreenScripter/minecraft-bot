package greenscripter.minecraft.packet.s2c.play.blocks;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SetChunkCenterPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:set_chunk_cache_center");

	public int x;
	public int z;

	public SetChunkCenterPacket() {

	}

	public SetChunkCenterPacket(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(x);
		out.writeVarInt(z);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		x = in.readVarInt();
		z = in.readVarInt();
	}

}
