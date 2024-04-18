package greenscripter.minecraft.packet.s2c.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SetContainerSlotPacket extends Packet {

	public int windowId;
	public int stateId;
	public short slotId;
	public Slot data;

	public SetContainerSlotPacket() {}

	public int id() {
		return 0x15;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		windowId = in.read();
		stateId = in.readVarInt();
		slotId = in.readShort();
		data = in.readSlot();
	}

}
