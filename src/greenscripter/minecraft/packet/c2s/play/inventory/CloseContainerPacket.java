package greenscripter.minecraft.packet.c2s.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class CloseContainerPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:container_close");

	public int windowId;

	public CloseContainerPacket() {}

	public CloseContainerPacket(int windowId) {
		this.windowId = windowId;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeByte(windowId);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		windowId = in.read();
	}

}
