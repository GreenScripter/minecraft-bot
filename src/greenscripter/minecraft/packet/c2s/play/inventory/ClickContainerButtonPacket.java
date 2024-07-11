package greenscripter.minecraft.packet.c2s.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ClickContainerButtonPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:container_button_click");

	public int windowId;
	public int buttonId;

	public ClickContainerButtonPacket() {}

	public ClickContainerButtonPacket(int windowId, int buttonId) {
		this.windowId = windowId;
		this.buttonId = buttonId;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeByte(windowId);
		out.writeByte(buttonId);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
