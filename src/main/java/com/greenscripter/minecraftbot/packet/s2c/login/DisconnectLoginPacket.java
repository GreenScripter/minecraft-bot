package com.greenscripter.minecraftbot.packet.s2c.login;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.nbt.NBTComponent;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class DisconnectLoginPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPacketId("login", "minecraft:login_disconnect");

	public NBTComponent reason;

	public DisconnectLoginPacket() {}

	public DisconnectLoginPacket(NBTComponent reason) {
		this.reason = reason;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeNBT(reason);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		reason = in.readNBT();
	}

}
