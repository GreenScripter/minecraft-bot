package greenscripter.minecraft.packet.c2s.login;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class LoginAcknowledgePacket extends Packet {

	public LoginAcknowledgePacket() {}

	public int id() {
		return 3;
	}

	public void toBytes(MCOutputStream out) throws IOException {}

	public void fromBytes(MCInputStream in) throws IOException {}

}
