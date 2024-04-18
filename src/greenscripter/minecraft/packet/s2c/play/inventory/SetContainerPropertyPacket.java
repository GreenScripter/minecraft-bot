package greenscripter.minecraft.packet.s2c.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SetContainerPropertyPacket extends Packet {

	public int windowId;
	public short property;
	public short value;

	public SetContainerPropertyPacket() {}

	public int id() {
		return 0x14;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		windowId = in.read();
		property = in.readShort();
		value = in.readShort();
	}

}
