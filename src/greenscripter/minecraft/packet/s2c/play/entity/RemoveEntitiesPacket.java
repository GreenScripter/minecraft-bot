package greenscripter.minecraft.packet.s2c.play.entity;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class RemoveEntitiesPacket extends Packet {

	public int[] ids;

	public RemoveEntitiesPacket() {}

	public int id() {
		return 0x40;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		int length = in.readVarInt();
		ids = new int[length];
		for (int i = 0; i < length; i++) {
			ids[i] = in.readVarInt();
		}
	}

}
