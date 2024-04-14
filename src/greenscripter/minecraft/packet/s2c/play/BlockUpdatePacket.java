package greenscripter.minecraft.packet.s2c.play;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Position;

public class BlockUpdatePacket extends Packet {

	public Position pos;
	public int state;

	public int id() {
		return 0x09;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		pos = in.readPosition();
		state = in.readVarInt();
	}

}
