package greenscripter.minecraft.packet.s2c.play;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class KeepAlivePacket extends Packet {

	public long value;
	public static final int id = 0x24;

	public KeepAlivePacket() {}

	public KeepAlivePacket(long value) {
		this.value = value;
	}

	public int id() {
		return id;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeLong(value);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		value = in.readLong();
	}

}
