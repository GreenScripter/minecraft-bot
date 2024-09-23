package greenscripter.minecraft.packet.s2c.play;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class AckBlockChangePacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:block_changed_ack");

	public int sequence;

	public AckBlockChangePacket() {}

	public AckBlockChangePacket(int sequence) {
		this.sequence = sequence;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(sequence);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		sequence = in.readVarInt();
	}

}
