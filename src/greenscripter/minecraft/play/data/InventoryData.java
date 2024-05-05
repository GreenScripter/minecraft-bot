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
	ServerConnection sc;

	public void init(ServerConnection sc) {
		this.sc = sc;
	}

	public OpenedScreen getActiveScreen() {
		if (screen == null) {
			return inv;
		}
		return screen;
	}

	public OpenedScreen getInventoryScreen() {
		if (screen == null || screen.slots == null || screen.slots.length == 0) {
			return inv;
		}
		return screen;
	}

	public void setHotbarSlot(int slot) {
		hotbarSlot = slot;
		sc.sendPacket(new HotbarSlotPacket(slot));
	}

	public void clickButton(int button) {
		sc.sendPacket(new ClickContainerButtonPacket(getActiveScreen().windowId, button));
	}

	public void closeScreen() {
		sc.sendPacket(new CloseContainerPacket(getActiveScreen().windowId));
		if (screen != null && screen.slots != null) {
			inv.replicateInvFrom(screen);
			previous = screen;
			screen = null;
		}
	}

	private void inventorySpillOver(Slot slot, Slot other, int cap) {
		int delta = cap - other.itemCount;
		if (delta >= slot.itemCount) {
			other.add(slot.itemCount);
			slot.setCount(0);
		} else {
			other.add(delta);
			slot.add(-delta);
		}
	}

	public void shiftClickSlot(Slot slot) {
		OpenedScreen screen = getActiveScreen();
		if (screen.slotIds.get(slot) == null) {
			System.err.println(slot + " is not part of the active screen " + screen.windowId);
			return;
		}
		//		if (!slot.present) return;//nothing being clicked.
		int slotId = screen.getSlotId(slot);
		ClickContainerPacket p = new ClickContainerPacket(screen.windowId, 1, 0, slotId);
		p.stateId = screen.stateId;
		if (slot.present) {
			int maxCount = slot.getItemInfo().maxStack;
			//player inventory shifts between hotbar and not, plus extra slots ex. armor
			if (screen.windowId == 0) {
				//check for armor and offhand moves first.
				boolean specialMoved = false;
				if (slot.getItemId().equals("minecraft:shield")) {
					if (!inv.getOffhand().present) {
						inv.getOffhand().become(slot);
						slot.setCount(0);
						p.changed.add(new SlotChange(screen.getSlotId(inv.getOffhand()), inv.getOffhand()));
						specialMoved = true;
					}
				} else if (slot.getItemInfo().isArmor) {
					Slot[] armor = inv.getArmor();
					Slot target = null;
					if (slot.getItemInfo().armorSlot.equals("HEAD")) {
						target = armor[0];
					}
					if (slot.getItemInfo().armorSlot.equals("CHEST")) {
						target = armor[1];
					}
					if (slot.getItemInfo().armorSlot.equals("LEGS")) {
						target = armor[2];
					}
					if (slot.getItemInfo().armorSlot.equals("FEET")) {
						target = armor[3];
					}
					if (target != null && !target.present) {
						target.become(slot);
						slot.setCount(0);
						p.changed.add(new SlotChange(screen.getSlotId(target), target));
						specialMoved = true;
					}
				}
				//if no special move, move normally
				if (!specialMoved) {
					if (screen.getSlotId(slot) > 35) {
						//hotbar sent to inv
						for (int i = 9; i < 36; i++) {
							Slot other = screen.getInventorySlot(i);
							if (other.equivalent(slot)) {

								if (other.itemCount == maxCount) continue;
								inventorySpillOver(slot, other, maxCount);
								p.changed.add(new SlotChange(screen.getSlotId(other), other));
								if (!slot.present) break;
							}
						}
						if (slot.present) for (int i = 9; i < 36; i++) {
							Slot other = screen.getInventorySlot(i);
							if (!other.present) {
								other.become(slot);
								slot.setCount(0);
								p.changed.add(new SlotChange(screen.getSlotId(other), other));
								break;
							}
						}
					} else {
						//inventory sent to hotbar
						for (int i = 0; i < 9; i++) {
							Slot other = screen.getHotbarSlot(i);
							if (other.equivalent(slot)) {

								if (other.itemCount == maxCount) continue;
								inventorySpillOver(slot, other, maxCount);
								p.changed.add(new SlotChange(screen.getSlotId(other), other));
								if (!slot.present) break;
							}
						}
						if (slot.present) for (int i = 0; i < 9; i++) {
							Slot other = screen.getHotbarSlot(i);
							if (!other.present) {
								other.become(slot);
								slot.setCount(0);
								p.changed.add(new SlotChange(screen.getSlotId(other), other));
								break;
							}
						}
					}
				}
			} else {
				//Other inventories shift between other and player but reversed.
				if (screen.getSlotId(slot) < screen.getOtherSlotsCount()) {
					//transfer into inventory
					//hotbar first
					for (int i = 0; i < 9; i++) {
						Slot other = screen.getHotbarSlot(i);
						if (other.equivalent(slot)) {
							if (other.itemCount == maxCount) continue;
							inventorySpillOver(slot, other, maxCount);
							p.changed.add(new SlotChange(screen.getSlotId(other), other));
							if (!slot.present) break;
						}
					}
					//rest of inv in reverse
					for (int i = 35; i >= 9; i--) {
						Slot other = screen.getInventorySlot(i);
						if (other.equivalent(slot)) {
							if (other.itemCount == maxCount) continue;
							inventorySpillOver(slot, other, maxCount);
							p.changed.add(new SlotChange(screen.getSlotId(other), other));
							if (!slot.present) break;
						}
					}
					//allow empty slots next
					//hotbar
					if (slot.present) for (int i = 0; i < 9; i++) {
						Slot other = screen.getHotbarSlot(i);
						if (!other.present) {
							other.become(slot);
							slot.setCount(0);
							p.changed.add(new SlotChange(screen.getSlotId(other), other));
							break;
						}
					}
					//rest of inv reversed
					if (slot.present) for (int i = 35; i >= 9; i--) {
						Slot other = screen.getInventorySlot(i);
						if (!other.present) {
							other.become(slot);
							slot.setCount(0);
							p.changed.add(new SlotChange(screen.getSlotId(other), other));
							break;
						}
					}
				} else {
					//transfer from inventory
					for (int i = 0; i < screen.getOtherSlotsCount(); i++) {
						Slot other = screen.getOtherSlot(i);
						if (other.equivalent(slot)) {

							if (other.itemCount == maxCount) continue;
							inventorySpillOver(slot, other, maxCount);
							p.changed.add(new SlotChange(screen.getSlotId(other), other));
							if (!slot.present) break;
						}
					}
					//allow empty slots next
					if (slot.present) for (int i = 0; i < screen.getOtherSlotsCount(); i++) {
						Slot other = screen.getOtherSlot(i);
						if (!other.present) {
							other.become(slot);
							slot.setCount(0);
							p.changed.add(new SlotChange(screen.getSlotId(other), other));
							break;
						}
					}
				}
			}
		}
		p.changed.add(new SlotChange(slotId, slot));
		p.carriedItem = screen.cursor;
		sc.sendPacket(p);
	}

	public void doubleClickSlot(Slot slot) {
		OpenedScreen screen = getActiveScreen();
		if (screen.slotIds.get(slot) == null) {
			System.err.println(slot + " is not part of the active screen " + screen.windowId);
			return;
		}
		if (!screen.cursor.present) return;//nothing in the cursor means nothing collected.
		if (screen.cursor.itemCount >= screen.cursor.getItemInfo().maxStack) return;//stack already full

		int slotId = screen.getSlotId(slot);
		ClickContainerPacket p = new ClickContainerPacket(screen.windowId, 6, 0, slotId);
		p.stateId = screen.stateId;
		int maxCount = screen.cursor.getItemInfo().maxStack;
		int spaceRemaining = maxCount - screen.cursor.itemCount;
		//take from partial stacks
		for (int i = 0; i < screen.slots.length; i++) {
			if (screen.slots[i].equivalent(screen.cursor)) {
				if (screen.slots[i].itemCount == maxCount) continue;
				int add = screen.slots[i].itemCount;
				screen.slots[i].add(-spaceRemaining);
				screen.cursor.add(Math.max(add - screen.slots[i].itemCount, 0));
				spaceRemaining = maxCount - screen.cursor.itemCount;
				p.changed.add(new SlotChange(i, screen.slots[i]));
				if (spaceRemaining == 0) break;
			}
		}
		//take from full stacks
		if (spaceRemaining != 0) {
			for (int i = 0; i < screen.slots.length; i++) {
				if (screen.slots[i].equivalent(screen.cursor)) {
					int add = screen.slots[i].itemCount;
					screen.slots[i].add(-spaceRemaining);
					screen.cursor.add(Math.max(add - screen.slots[i].itemCount, 0));
					spaceRemaining = maxCount - screen.cursor.itemCount;
					p.changed.add(new SlotChange(i, screen.slots[i]));
					if (spaceRemaining == 0) break;
				}
			}
		}

		p.changed.add(new SlotChange(slotId, slot));
		p.carriedItem = screen.cursor;
		sc.sendPacket(p);
	}

	public void swapOffhand(Slot slot) {
		swapSlots(slot, 40);
	}

	public void swapSlots(Slot slot, int hotbarSlot) {
		OpenedScreen screen = getActiveScreen();
		if (screen.slotIds.get(slot) == null) {
			System.err.println(slot + " is not part of the active screen " + screen.windowId);
			return;
		}
		if ((hotbarSlot < 0 || hotbarSlot > 8) && hotbarSlot != 40) return;
		int slotId = screen.getSlotId(slot);
		ClickContainerPacket p = new ClickContainerPacket(screen.windowId, 2, hotbarSlot, slotId);
		p.stateId = screen.stateId;

		Slot other = hotbarSlot == 40 ? inv.getOffhand() : screen.getHotbarSlot(hotbarSlot);
		//		if (!slot.present && !other.present) return;

		Slot temp = new Slot();
		temp.become(slot);

		slot.become(other);
		other.become(temp);

		p.changed.add(new SlotChange(slotId, slot));
		if (hotbarSlot != 40 || screen.windowId == 0) p.changed.add(new SlotChange(screen.getSlotId(other), other));
		p.carriedItem = screen.cursor;
		sc.sendPacket(p);
	}

	public void leftClickSlot(Slot slot) {
		OpenedScreen screen = getActiveScreen();
		if (screen.slotIds.get(slot) == null) {
			System.err.println(slot + " is not part of the active screen " + screen.windowId);
			return;
		}
		//		if (!slot.present && !screen.cursor.present) return;

		int slotId = screen.getSlotId(slot);
		ClickContainerPacket p = new ClickContainerPacket(screen.windowId, 0, 0, slotId);
		p.stateId = screen.stateId;
		if (slot.present || screen.cursor.present) {
			if (!slot.equivalent(screen.cursor)) {
				Slot temp = new Slot();
				temp.become(slot);
				slot.become(screen.cursor);
				screen.cursor.become(temp);
			} else {
				//			int original = slot.itemCount;

				int total = slot.itemCount;
				total += screen.cursor.itemCount;

				int maxStack = slot.getItemInfo().maxStack;

				slot.setCount(Math.min(total, maxStack));
				screen.cursor.setCount(total - maxStack);

				//			if (original == slot.itemCount) {
				//				return;// no change
				//			}
			}
		}
		p.changed.add(new SlotChange(slotId, slot));
		p.carriedItem = screen.cursor;
		sc.sendPacket(p);
	}

	public void rightClickSlot(Slot slot) {
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
				//				if (!slot.present) {
				//					return; //nothing in slot or cursor.
				//				}
				if (slot.present) {
					int count = slot.itemCount;
					screen.cursor.become(slot);
					slot.setCount(count / 2);
					screen.cursor.setCount(count - slot.itemCount);
				}
			}
		}
		p.changed.add(new SlotChange(slotId, slot));
		p.carriedItem = screen.cursor;
		sc.sendPacket(p);
	}

	public void dropItem(Slot slot) {
		OpenedScreen screen = getActiveScreen();
		if (screen.slotIds.get(slot) == null) {
			System.err.println(slot + " is not part of the active screen " + screen.windowId);
			return;
		}
		//		if (!slot.present) return;

		int slotId = screen.getSlotId(slot);
		ClickContainerPacket p = new ClickContainerPacket(screen.windowId, 4, 0, slotId);
		p.stateId = screen.stateId;
		slot.add(-1);
		p.changed.add(new SlotChange(slotId, slot));
		p.carriedItem = screen.cursor;
		sc.sendPacket(p);
	}

	public void dropCursorItem() {
		OpenedScreen screen = getActiveScreen();
		Slot slot = screen.cursor;

		ClickContainerPacket p = new ClickContainerPacket(screen.windowId, 0, 1, -999);
		p.stateId = screen.stateId;
		slot.add(-1);
		p.carriedItem = screen.cursor;
		sc.sendPacket(p);
	}

	public void dropAllCursorItems() {
		OpenedScreen screen = getActiveScreen();
		Slot slot = screen.cursor;

		ClickContainerPacket p = new ClickContainerPacket(screen.windowId, 0, 0, -999);
		p.stateId = screen.stateId;
		slot.setCount(0);
		p.carriedItem = screen.cursor;
		sc.sendPacket(p);
	}

	public void dropAllItems(Slot slot) {
		OpenedScreen screen = getActiveScreen();
		if (screen.slotIds.get(slot) == null) {
			System.err.println(slot + " is not part of the active screen " + screen.windowId);
			return;
		}
		//		if (!slot.present) return;

		int slotId = screen.getSlotId(slot);
		ClickContainerPacket p = new ClickContainerPacket(screen.windowId, 4, 1, slotId);
		p.stateId = screen.stateId;
		slot.setCount(0);
		p.changed.add(new SlotChange(slotId, slot));
		sc.sendPacket(p);
	}
}
