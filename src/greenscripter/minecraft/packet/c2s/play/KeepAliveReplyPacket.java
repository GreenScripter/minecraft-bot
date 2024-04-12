package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class KeepAliveReplyPacket extends Packet {

	public long value;

	public KeepAliveReplyPacket() {}

	public KeepAliveReplyPacket(long value) {
		this.value = value;
	}

	public int id() {
		return 0x15;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeLong(value);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		value = in.readLong();
	}

}
