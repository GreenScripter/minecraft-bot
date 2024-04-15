package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Position;

public class PlayerActionPacket extends Packet {

	public int status;
	public Position pos;
	public byte face;//Direction.DOWN.ordinal()
	public int sequence;//used for server response

	public PlayerActionPacket() {

	}

	public PlayerActionPacket(int status, Position pos, byte face, int sequence) {
		this.status = status;
		this.pos = pos;
		this.face = face;
		this.sequence = sequence;
	}

	public int id() {
		return 0x21;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(status);
		out.writePosition(pos);
		out.writeByte(face);
		out.writeVarInt(sequence);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

	public static final int START_MINING = 0;
	public static final int CANCEL_MINING = 1;
	public static final int FINISH_MINING = 2;
	public static final int DROP_STACK = 3;
	public static final int DROP_ITEM = 4;
	public static final int FINISH_USE = 5;
	public static final int SWAP_HANDS = 6;

}
