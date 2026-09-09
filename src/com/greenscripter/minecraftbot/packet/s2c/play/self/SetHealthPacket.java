package com.greenscripter.minecraftbot.packet.s2c.play.self;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class SetHealthPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:set_health");

	public float health;
	public int food;
	public float saturation;

	public SetHealthPacket() {}

	public SetHealthPacket(float health, int food, float saturation) {
		this.health = health;
		this.food = food;
		this.saturation = saturation;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeFloat(health);
		out.writeVarInt(food);
		out.writeFloat(saturation);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		health = in.readFloat();
		food = in.readVarInt();
		saturation = in.readFloat();
	}

}
