package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class TeleportConfirmPacket extends Packet {

	public int value;

	public TeleportConfirmPacket() {}

	public TeleportConfirmPacket(int value) {
		this.value = value;
	}

	public int id() {
		return 0;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(value);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		value = in.readVarInt();
	}

}
