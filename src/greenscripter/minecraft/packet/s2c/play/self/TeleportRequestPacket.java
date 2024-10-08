package greenscripter.minecraft.packet.s2c.play.self;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class TeleportRequestPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:player_position");

	public double x;
	public double y;
	public double z;
	public float yaw;
	public float pitch;
	public byte flags;
	public int teleportID;

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(z);
		out.writeFloat(yaw);
		out.writeFloat(pitch);
		out.writeByte(flags);
		out.writeVarInt(teleportID);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		x = in.readDouble();
		y = in.readDouble();
		z = in.readDouble();
		yaw = in.readFloat();
		pitch = in.readFloat();
		flags = in.readByte();
		teleportID = in.readVarInt();
	}

	public void makeNotRelative(double x, double y, double z, float pitch, float yaw) {
		if (flags == 0) return;
		if ((flags & 0x01) != 0) {
			this.x += x;
		}
		if ((flags & 0x02) != 0) {
			this.y += y;
		}
		if ((flags & 0x04) != 0) {
			this.z += z;
		}
		if ((flags & 0x08) != 0) {
			this.pitch += pitch;
		}
		if ((flags & 0x10) != 0) {
			this.yaw += yaw;
		}
		flags = 0;
	}

}
