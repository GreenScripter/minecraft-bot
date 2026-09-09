package com.greenscripter.minecraftbot.packet.s2c.play.inventory;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.play.inventory.Slot;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class SetContainerSlotPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:container_set_slot");

	public int windowId;
	public int stateId;
	public short slotId;
	public Slot data;

	public SetContainerSlotPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		windowId = in.readByte();
		stateId = in.readVarInt();
		slotId = in.readShort();
		data = in.readSlot();
		//		System.out.println(data.toStringShort());
	}

}
