package greenscripter.minecraft.packet.s2c.play;

import java.io.IOException;

import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class DisconnectPacket extends Packet {

	public NBTComponent reason;

	public DisconnectPacket() {}

	public int id() {
		return 0x1B;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		reason = in.readNBT();
	}

}
