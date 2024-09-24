package greenscripter.minecraft.packet.s2c.login;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class DisconnectLoginPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPacketId("login", "minecraft:login_disconnect");

	public NBTComponent reason;

	public DisconnectLoginPacket() {}

	public DisconnectLoginPacket(NBTComponent reason) {
		this.reason = reason;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeNBT(reason);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		reason = in.readNBT();
	}

}
