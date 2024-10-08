package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class UseItemPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:use_item");

	public int hand;
	public int sequence;//used for server response

	public UseItemPacket() {

	}

	public UseItemPacket(int hand, int sequence) {
		this.hand = hand;
		this.sequence = sequence;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(hand);
		out.writeVarInt(sequence);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

	public static final int MAIN_HAND = 0;
	public static final int OFF_HAND = 1;

}
