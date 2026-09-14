package com.greenscripter.minecraftbot.packet.s2c.play;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class ServerPingPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:ping");

	public int value;

	public ServerPingPacket() {}

	public ServerPingPacket(int value) {
		this.value = value;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeInt(value);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		value = in.readInt();
	}

}
