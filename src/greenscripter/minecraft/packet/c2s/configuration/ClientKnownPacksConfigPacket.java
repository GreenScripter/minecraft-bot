package greenscripter.minecraft.packet.c2s.configuration;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ClientKnownPacksConfigPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPacketId("configuration", "minecraft:select_known_packs");

	public String[] namespaces = {};
	public String[] ids = {};
	public String[] versions = {};

	public ClientKnownPacksConfigPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(namespaces.length);
		for (int i = 0; i < namespaces.length; i++) {
			out.writeString(namespaces[i]);
			out.writeString(ids[i]);
			out.writeString(versions[i]);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
