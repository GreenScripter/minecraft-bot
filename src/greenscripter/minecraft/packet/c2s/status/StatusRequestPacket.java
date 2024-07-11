package greenscripter.minecraft.packet.c2s.status;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class StatusRequestPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPacketId("status", "minecraft:status_request");

	public StatusRequestPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {}

	public void fromBytes(MCInputStream in) throws IOException {}

}
