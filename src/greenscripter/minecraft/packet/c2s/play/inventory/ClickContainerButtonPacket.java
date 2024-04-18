package greenscripter.minecraft.packet.c2s.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ClickContainerButtonPacket extends Packet {

	public int windowId;
	public int buttonId;

	public ClickContainerButtonPacket() {}

	public ClickContainerButtonPacket(int windowId) {
		this.windowId = windowId;
	}

	public int id() {
		return 0x0C;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeByte(windowId);
		out.writeByte(buttonId);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
