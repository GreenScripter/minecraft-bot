package com.greenscripter.minecraftbot.packet.s2c.configuration;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.nbt.NBTComponent;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class DisconnectConfigPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPacketId("configuration", "minecraft:disconnect");

	public NBTComponent reason;

	public DisconnectConfigPacket() {}

	public DisconnectConfigPacket(NBTComponent reason) {
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
