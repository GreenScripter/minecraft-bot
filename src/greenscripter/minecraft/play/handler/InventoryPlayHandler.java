package greenscripter.minecraft.play.handler;

import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.s2c.play.inventory.ForceCloseContainerPacket;
import greenscripter.minecraft.packet.s2c.play.inventory.OpenContainerPacket;
import greenscripter.minecraft.packet.s2c.play.inventory.SetContainerContentPacket;
import greenscripter.minecraft.packet.s2c.play.inventory.SetContainerPropertyPacket;
import greenscripter.minecraft.packet.s2c.play.inventory.SetContainerSlotPacket;
import greenscripter.minecraft.play.data.InventoryData;
import greenscripter.minecraft.play.inventory.OpenedScreen;

public class InventoryPlayHandler extends PlayHandler {

	int setContentId = new SetContainerContentPacket().id();
	int setPropertyId = new SetContainerPropertyPacket().id();
	int setSlotId = new SetContainerSlotPacket().id();
	int closeId = new ForceCloseContainerPacket().id();
	int openId = new OpenContainerPacket().id();

	public void handlePacket(UnknownPacket up, ServerConnection sc) throws IOException {
		InventoryData data = sc.getData(InventoryData.class);
		if (up.id == setContentId) {
			SetContainerContentPacket p = up.convert(new SetContainerContentPacket());
			OpenedScreen screen = p.windowId == 0 ? data.inv : data.getActiveScreen();
			if (screen.windowId == p.windowId) {
				screen.stateId = p.stateId;
				if (screen.slots == null) {
					screen.initWithSlotCount(p.slots.length);
				}
				for (int i = 0; i < p.slots.length; i++) {
					screen.slots[i].become(p.slots[i]);
				}
				screen.cursor.become(p.cursor);
				System.out.println("replace " + screen.stateId);
			}
		} else if (up.id == setPropertyId) {
			SetContainerPropertyPacket p = up.convert(new SetContainerPropertyPacket());
			OpenedScreen screen = p.windowId == 0 ? data.inv : data.getActiveScreen();
			if (screen.windowId == p.windowId) {
				screen.properties.put(p.property, p.value);
			}
		} else if (up.id == setSlotId) {
			SetContainerSlotPacket p = up.convert(new SetContainerSlotPacket());
			OpenedScreen screen = p.windowId == 0 ? data.inv : data.getActiveScreen();
			if (screen.windowId == p.windowId) {
				screen.stateId = p.stateId;
				screen.slots[p.slotId].become(p.data);
				System.out.println("update " + screen.stateId + " " + p.slotId + " " + p.data);
			} else if (screen.windowId == 0 && data.previous != null && data.previous.slots != null && data.previous.windowId == p.windowId) {
				int slotCount = data.previous.slots.length;
				int endDelta = slotCount - p.slotId;
				int slot = screen.slots.length - endDelta;
				if (endDelta <= 36) {
					screen.slots[slot].become(p.data);
					System.out.println("passed along update from " + p.windowId + " to " + screen.stateId + " " + p.slotId + " to " + slot + " " + p.data);
				}
			}
		} else if (up.id == closeId) {
			ForceCloseContainerPacket p = up.convert(new ForceCloseContainerPacket());
			OpenedScreen screen = p.windowId == 0 ? data.inv : data.getActiveScreen();
			if (screen.windowId == p.windowId) {
				if (screen.windowId != 0) {
					if (data.screen != null && data.screen.slots != null) {
						data.inv.replicateInvFrom(data.screen);
					}
					data.previous = data.screen;
					data.screen = null;
				}
			}
		} else if (up.id == openId) {
			OpenContainerPacket p = up.convert(new OpenContainerPacket());
			OpenedScreen screen = p.windowId == 0 ? data.inv : data.getActiveScreen();
			if (screen.windowId == p.windowId) {
				screen.title = p.title;
				screen.windowType = p.windowType;
			} else {
				screen = (data.screen = new OpenedScreen());
				screen.windowId = p.windowId;
				screen.windowType = p.windowType;
				screen.title = p.title;

			}
		}

	}

	public List<Integer> handlesPackets() {
		return List.of(setContentId, setPropertyId, setSlotId, closeId, openId);
	}
}
