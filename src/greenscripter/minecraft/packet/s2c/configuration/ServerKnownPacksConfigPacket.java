package greenscripter.minecraft.packet.s2c.configuration;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ServerKnownPacksConfigPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPacketId("configuration", "minecraft:select_known_packs");

	public String[] namespaces;
	public String[] ids;
	public String[] versions;

	public ServerKnownPacksConfigPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		int count = in.readVarInt();

		namespaces = new String[count];
		ids = new String[count];
		versions = new String[count];

		for (int i = 0; i < count; i++) {
			namespaces[i] = in.readString();
			ids[i] = in.readString();
			versions[i] = in.readString();
		}
	}

}
