package com.greenscripter.minecraftbot.packet.c2s.play.inventory;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.play.inventory.Slot;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class SetCreativeSlotPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:set_creative_mode_slot");

	public int slotId;
	public Slot slot;

	public SetCreativeSlotPacket() {}

	public SetCreativeSlotPacket(int slotId, Slot slot) {
		this.slot = slot;
		this.slotId = slotId;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeShort(slotId);
		out.writeSlot(slot);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		slotId = in.readShort();
		slot = in.readSlot();
	}

}
