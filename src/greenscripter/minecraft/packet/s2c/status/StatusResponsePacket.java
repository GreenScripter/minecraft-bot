package greenscripter.minecraft.packet.s2c.status;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class StatusResponsePacket extends Packet {

	public static final int packetId = PacketIds.getS2CPacketId("status", "minecraft:status_response");

	public String value;

	public StatusResponsePacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeString(value);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		value = in.readString();
	}

}
