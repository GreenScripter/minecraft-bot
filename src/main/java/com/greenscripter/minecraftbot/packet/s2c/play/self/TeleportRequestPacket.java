package com.greenscripter.minecraftbot.packet.s2c.play.self;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class TeleportRequestPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:player_position");

	public double x;
	public double y;
	public double z;
	public double dx;
	public double dy;
	public double dz;
	public float yaw;
	public float pitch;
	public byte flags;
	public int teleportID;

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(teleportID);
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(z);
		out.writeDouble(dx);
		out.writeDouble(dy);
		out.writeDouble(dz);
		out.writeFloat(yaw);
		out.writeFloat(pitch);
		out.writeByte(flags);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		teleportID = in.readVarInt();
		x = in.readDouble();
		y = in.readDouble();
		z = in.readDouble();
		dx = in.readDouble();
		dy = in.readDouble();
		dz = in.readDouble();
		yaw = in.readFloat();
		pitch = in.readFloat();
		flags = in.readByte();
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
