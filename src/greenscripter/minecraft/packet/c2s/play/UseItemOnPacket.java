package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.Direction;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Position;

public class UseItemOnPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:use_item_on");

	public int hand;
	public Position pos;
	public int face = (byte) Direction.UP.ordinal();
	public float cursorX = 0.5f;
	public float cursorY = 0.5f;
	public float cursorZ = 0.5f;
	public boolean inside;
	public int sequence;//used for server response

	public UseItemOnPacket() {

	}

	public UseItemOnPacket(int hand, Position pos, int face, int sequence) {
		this.hand = hand;
		this.pos = pos;
		this.face = face;
		this.sequence = sequence;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(hand);
		out.writePosition(pos);
		out.writeVarInt(face);
		out.writeFloat(cursorX);
		out.writeFloat(cursorY);
		out.writeFloat(cursorZ);
		out.writeBoolean(inside);
		out.writeVarInt(sequence);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

	public static final int MAIN_HAND = 0;
	public static final int OFF_HAND = 1;

}
