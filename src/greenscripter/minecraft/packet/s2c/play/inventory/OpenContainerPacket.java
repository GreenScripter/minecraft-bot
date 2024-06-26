package greenscripter.minecraft.packet.s2c.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class OpenContainerPacket extends Packet {

	public int windowId;
	public int windowType;
	public NBTComponent title;

	public OpenContainerPacket() {}

	public int id() {
		return 0x31;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		windowId = in.readVarInt();
		windowType = in.readVarInt();
		title = in.readNBT();
	}

}
