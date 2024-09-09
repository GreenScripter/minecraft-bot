package greenscripter.minecraft.packet.s2c.status;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PingResponsePacket extends Packet {

	public static final int packetId = PacketIds.getS2CPacketId("status", "minecraft:pong_response");

	public long value;

	public PingResponsePacket() {}

	public PingResponsePacket(long value) {
		this.value = value;
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
