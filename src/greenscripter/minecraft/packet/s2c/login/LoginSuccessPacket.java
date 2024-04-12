package greenscripter.minecraft.packet.s2c.login;

import java.util.UUID;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class LoginSuccessPacket extends Packet {

	public String name;
	public UUID uuid;
	public int properties;

	public LoginSuccessPacket() {}

	public int id() {
		return 2;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		uuid = in.readUUID();
		name = in.readString();
		properties = in.readVarInt();
	}

}
