package com.greenscripter.minecraftbot.packet.s2c.play.inventory;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.nbt.NBTComponent;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.play.inventory.OpenedScreen;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class OpenContainerPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:open_screen");

	public int windowId;
	public int windowType;
	public NBTComponent title;

	public OpenContainerPacket() {}

	public OpenContainerPacket(OpenedScreen screen) {
		windowId = screen.windowId;
		windowType = screen.windowType;
		title = screen.title;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(windowId);
		out.writeVarInt(windowType);
		out.writeNBT(title);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		windowId = in.readVarInt();
		windowType = in.readVarInt();
		title = in.readNBT();
	}

}
