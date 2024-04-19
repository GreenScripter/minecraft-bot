package greenscripter.minecraft.play.data;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.c2s.play.inventory.ClickContainerButtonPacket;
import greenscripter.minecraft.packet.c2s.play.inventory.ClickContainerPacket;
import greenscripter.minecraft.packet.c2s.play.inventory.ClickContainerPacket.SlotChange;
import greenscripter.minecraft.packet.c2s.play.inventory.CloseContainerPacket;
import greenscripter.minecraft.packet.c2s.play.inventory.HotbarSlotPacket;
import greenscripter.minecraft.play.inventory.OpenedScreen;
import greenscripter.minecraft.play.inventory.PlayerInventoryScreen;
import greenscripter.minecraft.play.inventory.Slot;

public class InventoryData extends PlayData {

	public PlayerInventoryScreen inv = new PlayerInventoryScreen();
	public OpenedScreen screen;
	public int hotbarSlot;

	public OpenedScreen previous;

	public OpenedScreen getActiveScreen() {
		if (screen == null) {
			return inv;
		}
		return screen;
	}

	public void setHotbarSlot(ServerConnection sc, int slot) {
		hotbarSlot = slot;
		sc.sendPacket(new HotbarSlotPacket(slot));
	}

	public void clickButton(ServerConnection sc, int button) {
		sc.sendPacket(new ClickContainerButtonPacket(getActiveScreen().windowId, button));
	}

	public void closeScreen(ServerConnection sc) {
		sc.sendPacket(new CloseContainerPacket(getActiveScreen().windowId));
		if (screen != null && screen.slots != null) {
			inv.replicateInvFrom(screen);
			previous = screen;
			screen = null;
		}
	}

	public void leftClickSlot(ServerConnection sc, Slot slot) {
		OpenedScreen screen = getActiveScreen();
		if (screen.slotIds.get(slot) == null) {
			System.err.println(slot + " is not part of the active screen " + screen.windowId);
			return;
		}
		if (!slot.present && !screen.cursor.present) return;

		int slotId = screen.getSlotId(slot);
		ClickContainerPacket p = new ClickContainerPacket(screen.windowId, 0, 0, slotId);
		p.stateId = screen.stateId;
		if (!slot.equivalent(screen.cursor)) {
			Slot temp = new Slot();
			temp.become(slot);
			slot.become(screen.cursor);
			screen.cursor.become(temp);
		} else {
			int original = slot.itemCount;

			int total = slot.itemCount;
			total += screen.cursor.itemCount;

			int maxStack = slot.getItemInfo().maxStack;

			slot.setCount(Math.min(total, maxStack));
			screen.cursor.setCount(total - maxStack);

			if (original == slot.itemCount) {
				return;// no change
			}
		}
		p.changed.add(new SlotChange(slotId, slot));
		p.carriedItem = screen.cursor;
		sc.sendPacket(p);
	}

	public void rightClickSlot(ServerConnection sc, Slot slot) {
		OpenedScreen screen = getActiveScreen();
		if (screen.slotIds.get(slot) == null) {
			System.err.println(slot + " is not part of the active screen " + screen.windowId);
			return;
		}
		int slotId = screen.getSlotId(slot);
		ClickContainerPacket p = new ClickContainerPacket(screen.windowId, 0, 1, slotId);
		p.stateId = screen.stateId;

		if (screen.cursor.present && slot.present && !slot.equivalent(screen.cursor)) {
			//cursor present and does not match, swap slots.
			Slot temp = new Slot();
			temp.become(slot);
			slot.become(screen.cursor);
			screen.cursor.become(temp);
		} else {
			if (screen.cursor.present) {
				//cursor matches, deploy 1.
				int maxStack = screen.cursor.getItemInfo().maxStack;
				if (slot.itemCount >= maxStack) {
					return;//stack already full
				}
				if (!slot.present) {
					slot.become(screen.cursor);
					slot.setCount(1);
				} else {
					slot.add(1);
				}
				screen.cursor.add(-1);
			} else {
				//nothing in cursor, split stack.
				if (!slot.present) {
					return; //nothing in slot or cursor.
				}
				int count = slot.itemCount;
				screen.cursor.become(slot);
				slot.setCount(count / 2);
				screen.cursor.setCount(count - slot.itemCount);
			}
		}
		p.changed.add(new SlotChange(slotId, slot));
		p.carriedItem = screen.cursor;
		sc.sendPacket(p);
	}

	public void dropItem(ServerConnection sc, Slot slot) {
		OpenedScreen screen = getActiveScreen();
		if (screen.slotIds.get(slot) == null) {
			System.err.println(slot + " is not part of the active screen " + screen.windowId);
			return;
		}
		if (!slot.present) return;

		int slotId = screen.getSlotId(slot);
		ClickContainerPacket p = new ClickContainerPacket(screen.windowId, 4, 0, slotId);
		p.stateId = screen.stateId;
		slot.add(-1);
		p.changed.add(new SlotChange(slotId, slot));
		p.carriedItem = screen.cursor;
		sc.sendPacket(p);
	}

	public void dropCursorItem(ServerConnection sc) {
		OpenedScreen screen = getActiveScreen();
		Slot slot = screen.cursor;

		ClickContainerPacket p = new ClickContainerPacket(screen.windowId, 0, 1, -999);
		p.stateId = screen.stateId;
		slot.add(-1);
		p.carriedItem = screen.cursor;
		sc.sendPacket(p);
	}

	public void dropAllCursorItems(ServerConnection sc) {
		OpenedScreen screen = getActiveScreen();
		Slot slot = screen.cursor;

		ClickContainerPacket p = new ClickContainerPacket(screen.windowId, 0, 0, -999);
		p.stateId = screen.stateId;
		slot.setCount(0);
		p.carriedItem = screen.cursor;
		sc.sendPacket(p);
	}

	public void dropAllItems(ServerConnection sc, Slot slot) {
		OpenedScreen screen = getActiveScreen();
		if (screen.slotIds.get(slot) == null) {
			System.err.println(slot + " is not part of the active screen " + screen.windowId);
			return;
		}
		if (!slot.present) return;

		int slotId = screen.getSlotId(slot);
		ClickContainerPacket p = new ClickContainerPacket(screen.windowId, 4, 1, slotId);
		p.stateId = screen.stateId;
		slot.setCount(0);
		p.changed.add(new SlotChange(slotId, slot));
		sc.sendPacket(p);
	}
}
