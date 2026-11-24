package com.greenscripter.minecraftbot.packet.s2c.play.entity;

import java.util.List;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class TeleportEntityPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:entity_position_sync");

	public int entityID;
	public int pathType;
	public List<PositonStep> steps;
	public double x;
	public double y;
	public double z;
	public byte yaw;
	public byte pitch;
	public boolean onGround;

	public TeleportEntityPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		pathType = in.readVarInt();
		if (pathType == 0) {
			x = in.readDouble();
			y = in.readDouble();
			z = in.readDouble();
		} else if (pathType == 1) {
			steps = in.readPrefixedArray(() -> {
				var p = new PositonStep();
				p.x = in.readDouble();
				p.y = in.readDouble();
				p.z = in.readDouble();
				p.tickOffset = in.readVarInt();
				return p;
			});
		}
		yaw = in.readByte();
		pitch = in.readByte();
		onGround = in.readBoolean();
	}

	public static class PositonStep {

		public double x;
		public double y;
		public double z;
		int tickOffset;
	}

}
