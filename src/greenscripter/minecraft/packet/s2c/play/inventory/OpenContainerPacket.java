package greenscripter.minecraft.packet.s2c.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.play.inventory.OpenedScreen;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class OpenContainerPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:open_screen");

	public int windowId;
	public int windowType;
	public NBTComponent title;

	public OpenContainerPacket() {}

	public OpenContainerPacket(OpenedScreen screen) {
		windowId = screen.windowId;
		windowType = screen.windowType;
		title = screen.title;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(windowId);
		out.writeVarInt(windowType);
		out.writeNBT(title);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		windowId = in.readVarInt();
		windowType = in.readVarInt();
		title = in.readNBT();
	}

}
