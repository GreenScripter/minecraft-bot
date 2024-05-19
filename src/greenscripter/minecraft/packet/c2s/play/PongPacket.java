package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PongPacket extends Packet {

	public int value;

	public PongPacket() {}

	public PongPacket(int value) {
		this.value = value;
	}

	public int id() {
		return 0x24;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeInt(value);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		value = in.readInt();
	}

}
