package greenscripter.minecraft.packet.s2c.login;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SetCompressionPacket extends Packet {

	public int value;

	public SetCompressionPacket() {}

	public int id() {
		return 3;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(value);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		value = in.readVarInt();
	}

}
