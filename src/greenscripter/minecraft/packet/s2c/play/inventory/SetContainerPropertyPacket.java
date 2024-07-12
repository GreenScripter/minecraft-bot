package greenscripter.minecraft.packet.s2c.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SetContainerPropertyPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:container_set_data");

	public int windowId;
	public short property;
	public short value;

	public SetContainerPropertyPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		windowId = in.read();
		property = in.readShort();
		value = in.readShort();
	}

}
