package greenscripter.minecraft.packet.c2s.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class CloseContainerPacket extends Packet {

	public int windowId;

	public CloseContainerPacket() {}

	public CloseContainerPacket(int windowId) {
		this.windowId = windowId;
	}

	public int id() {
		return 0x0E;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeByte(windowId);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		windowId = in.read();
	}

}
