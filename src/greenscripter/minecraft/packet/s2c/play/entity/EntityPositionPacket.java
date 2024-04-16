package greenscripter.minecraft.packet.s2c.play.entity;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class EntityPositionPacket extends Packet {

	public int entityID;
	public short deltaX;
	public short deltaY;
	public short deltaZ;
	public boolean onGround;

	public EntityPositionPacket() {}

	public int id() {
		return 0x2C;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		deltaX = in.readShort();
		deltaY = in.readShort();
		deltaZ = in.readShort();
		onGround = in.readBoolean();
	}

	public static double getFrom(double previous, short delta) {
		return (delta / 128.0) / 32.0 + previous;//(currentX * 32 - prevX * 32) * 128;
	}

}
