package greenscripter.minecraft.packet.c2s.configuration;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class KeepAliveReplyConfigPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPacketId("configuration", "minecraft:keep_alive");

	public long value;

	public KeepAliveReplyConfigPacket() {}

	public KeepAliveReplyConfigPacket(long value) {
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
