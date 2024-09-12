package greenscripter.minecraft.packet.c2s.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class HotbarSlotPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:set_carried_item");

	public int slot;

	public HotbarSlotPacket() {}

	public HotbarSlotPacket(int slot) {
		this.slot = slot;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeShort(slot);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		slot = in.readShort();
	}

}
