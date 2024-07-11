package greenscripter.minecraft.packet.c2s.status;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PingRequestPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPacketId("status", "minecraft:ping_request");

	public long value;

	public PingRequestPacket() {}

	public PingRequestPacket(long v) {
		this.value = v;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeLong(value);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		value = in.readLong();
	}

}
