package greenscripter.minecraft.packet.s2c.play.entity;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class EntityEquipmentPacket extends Packet {

	public int entityID;
	public Slot[] slots = new Slot[6];

	private static final byte NEXT_BIT = (byte) (1 << 7);

	public EntityEquipmentPacket() {}

	public int id() {
		return 0x59;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		byte slotId = in.readByte();
		while ((slotId & NEXT_BIT) != 0) {
			slotId = (byte) (slotId ^ NEXT_BIT);
			Slot slot = in.readSlot();
			slots[slotId] = slot;

			slotId = in.readByte();
		}
		Slot slot = in.readSlot();
		slots[slotId] = slot;
	}

}
