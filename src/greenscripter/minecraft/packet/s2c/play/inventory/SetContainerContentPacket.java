package greenscripter.minecraft.packet.s2c.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SetContainerContentPacket extends Packet {

	public int windowId;
	public int stateId;
	public Slot[] slots;
	public Slot cursor;

	public SetContainerContentPacket() {}

	public int id() {
		return 0x13;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		windowId = in.read();
		stateId = in.readVarInt();
		int length = in.readVarInt();
		slots = new Slot[length];
		for (int i = 0; i < length; i++) {
			slots[i] = in.readSlot();
		}
		cursor = in.readSlot();
	}

}
