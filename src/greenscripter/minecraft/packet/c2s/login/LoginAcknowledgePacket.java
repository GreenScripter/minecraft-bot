package greenscripter.minecraft.packet.c2s.login;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class LoginAcknowledgePacket extends Packet {

	public static final int packetId = PacketIds.getC2SPacketId("login", "minecraft:login_acknowledged");

	public LoginAcknowledgePacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {}

	public void fromBytes(MCInputStream in) throws IOException {}

}
