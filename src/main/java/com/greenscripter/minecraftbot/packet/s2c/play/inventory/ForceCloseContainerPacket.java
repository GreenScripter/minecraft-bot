package com.greenscripter.minecraftbot.packet.s2c.play.inventory;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class ForceCloseContainerPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:container_close");

	public int windowId;

	public ForceCloseContainerPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		windowId = in.read();
	}

}
