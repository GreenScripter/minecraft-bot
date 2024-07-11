package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class TeleportConfirmPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:accept_teleportation");

	public int value;

	public TeleportConfirmPacket() {}

	public TeleportConfirmPacket(int value) {
		this.value = value;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(value);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		value = in.readVarInt();
	}

}
