package greenscripter.minecraft.packet.s2c.configuration;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ServerKnownPacksConfigPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPacketId("configuration", "minecraft:select_known_packs");

	public String[] namespaces = { "minecraft" };
	public String[] ids = { "core" };
	public String[] versions = { "1.21" };

	public ServerKnownPacksConfigPacket() {}

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
		int count = in.readVarInt();

		namespaces = new String[count];
		ids = new String[count];
		versions = new String[count];

		for (int i = 0; i < count; i++) {
			namespaces[i] = in.readString();
			ids[i] = in.readString();
			versions[i] = in.readString();
			System.out.println("Known pack: " + namespaces[i] + " id " + ids[i] + " version " + versions[i]);
		}
	}

}
