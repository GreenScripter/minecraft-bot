package greenscripter.minecraft.packet.s2c.play.entity;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PickupItemPacket extends Packet {

	public int entityID;
	public int collectorEntityID;
	public int count;

	public PickupItemPacket() {}

	public int id() {
		return 0x6C;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		collectorEntityID = in.readVarInt();
		count = in.readVarInt();
	}

}
