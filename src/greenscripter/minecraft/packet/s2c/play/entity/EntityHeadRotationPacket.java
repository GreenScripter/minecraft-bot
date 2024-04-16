package greenscripter.minecraft.packet.s2c.play.entity;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class EntityHeadRotationPacket extends Packet {

	public int entityID;
	public byte headYaw;

	public EntityHeadRotationPacket() {}

	public int id() {
		return 0x46;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		headYaw = in.readByte();
	}

}
