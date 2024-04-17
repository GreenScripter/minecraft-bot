package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.play.data.PlayerData;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PlayerMovePositionRotationPacket extends Packet {

	public double x;
	public double y;
	public double z;
	public float yaw;
	public float pitch;
	public boolean onGround = true;

	public PlayerMovePositionRotationPacket() {}

	public PlayerMovePositionRotationPacket(PlayerData data) {
		this(data.pos.pos.x, data.pos.pos.y, data.pos.pos.z, data.pos.yaw, data.pos.pitch);
	}

	public PlayerMovePositionRotationPacket(double x2, double y2, double z2, float yaw2, float pitch2) {
		this.x = x2;
		this.y = y2;
		this.z = z2;
		this.yaw = yaw2;
		this.pitch = pitch2;
	}

	public int id() {
		return 0x18;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(z);
		out.writeFloat(yaw);
		out.writeFloat(pitch);
		out.writeBoolean(onGround);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
