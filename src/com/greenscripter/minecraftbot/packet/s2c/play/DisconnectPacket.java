package com.greenscripter.minecraftbot.packet.s2c.play;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.nbt.NBTComponent;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class DisconnectPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:disconnect");

	public NBTComponent reason;

	public DisconnectPacket() {}

	public DisconnectPacket(NBTComponent reason) {
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
