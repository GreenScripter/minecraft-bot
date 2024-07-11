package greenscripter.minecraft.packet.s2c.login;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SetCompressionPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPacketId("login", "minecraft:login_compression");

	public int value;

	public SetCompressionPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(value);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		value = in.readVarInt();
	}

}
