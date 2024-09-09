package greenscripter.minecraft.packet.s2c.play;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class TransferPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:transfer");

	public String host;
	public int port;

	public TransferPacket() {}

	public TransferPacket(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeString(host);
		out.writeVarInt(port);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		host = in.readString();
		port = in.readVarInt();
	}

}
