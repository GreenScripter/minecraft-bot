package com.greenscripter.minecraftbot.packet.c2s.play;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class TeleportConfirmPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:accept_teleportation");

	public int value;
	public double x;
	public double y;
	public double z;
	public float yaw;
	public float pitch;

	public TeleportConfirmPacket() {}

	public TeleportConfirmPacket(int value) {
		this.value = value;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(value);
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(z);
		out.writeFloat(yaw);
		out.writeFloat(pitch);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		value = in.readVarInt();
		x = in.readDouble();
		y = in.readDouble();
		z = in.readDouble();
		yaw = in.readFloat();
		pitch = in.readFloat();
	}

}
