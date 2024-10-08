package greenscripter.minecraft.packet.s2c.play.blocks;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Position;

public class BlockUpdatePacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:block_update");

	public Position pos;
	public int state;

	public BlockUpdatePacket() {

	}

	public BlockUpdatePacket(Position pos, int state) {
		this.pos = pos;
		this.state = state;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writePosition(pos);
		out.writeVarInt(state);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		pos = in.readPosition();
		state = in.readVarInt();
	}

}
