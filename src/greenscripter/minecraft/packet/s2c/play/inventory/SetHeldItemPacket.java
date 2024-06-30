package greenscripter.minecraft.packet.s2c.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SetHeldItemPacket extends Packet {

	public byte slot;

	public SetHeldItemPacket() {}

	public int id() {
		return 0x51;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		slot = in.readByte();
	}

}
