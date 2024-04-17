package greenscripter.minecraft.packet.s2c.play.self;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SetHealthPacket extends Packet {

	public float health;
	public int food;
	public float saturation;

	public SetHealthPacket() {}

	public int id() {
		return 0x5B;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		health = in.readFloat();
		food = in.readVarInt();
		saturation = in.readFloat();
	}

}
