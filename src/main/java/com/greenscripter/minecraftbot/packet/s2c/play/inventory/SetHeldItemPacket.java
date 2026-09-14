package com.greenscripter.minecraftbot.packet.s2c.play.inventory;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class SetHeldItemPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:set_carried_item");

	public byte slot;

	public SetHeldItemPacket() {}

	public SetHeldItemPacket(byte slot) {
		this.slot = slot;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeByte(slot);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		slot = in.readByte();
	}

}
