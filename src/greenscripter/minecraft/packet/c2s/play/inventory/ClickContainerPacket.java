package greenscripter.minecraft.packet.c2s.play.inventory;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ClickContainerPacket extends Packet {

	public int windowId;
	public int stateId;
	public int slot;
	public int button;
	public int mode;
	public List<SlotChange> changed = new ArrayList<>();
	public Slot carriedItem = new Slot();

	public ClickContainerPacket() {}

	public ClickContainerPacket(int windowId, int mode, int buttonId, int slot) {
		this.windowId = windowId;
		this.button = buttonId;
		this.mode = mode;
		this.slot = slot;
	}

	public int id() {
		return 0x0D;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeByte(windowId);
		out.writeVarInt(stateId);
		out.writeShort(slot);
		out.writeByte(button);
		out.writeVarInt(mode);
		out.writeVarInt(changed.size());
		for (SlotChange sc : changed) {
			out.writeShort(sc.index);
			out.writeSlot(sc.slot);
		}
		out.writeSlot(carriedItem);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

	public static class SlotChange {

		public int index;
		public Slot slot;

		public SlotChange() {

		}

		public SlotChange(int index, Slot slot) {
			this.index = index;
			this.slot = slot;
		}

	}
}
