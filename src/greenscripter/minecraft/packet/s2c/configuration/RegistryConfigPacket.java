package greenscripter.minecraft.packet.s2c.configuration;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.DynamicRegistry;
import greenscripter.minecraft.utils.DynamicRegistry.RegistryEntry;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class RegistryConfigPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPacketId("configuration", "minecraft:registry_data");

	public DynamicRegistry registry;

	public RegistryConfigPacket() {}

	public RegistryConfigPacket(DynamicRegistry registry) {
		this.registry = registry;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeString(registry.name);
		out.writeVarInt(registry.registry.length);
		for (int i = 0; i < registry.registry.length; i++) {
			RegistryEntry e = registry.registry[i];

			out.writeString(e.entryId);
			out.writeBoolean(e.hasData);
			if (e.hasData) {
				out.writeNBT(e.data);
			}
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		registry = new DynamicRegistry();
		registry.name = in.readString();
		registry.registry = new RegistryEntry[in.readVarInt()];
		for (int i = 0; i < registry.registry.length; i++) {
			RegistryEntry e = new RegistryEntry();
			e.id = i;
			e.entryId = in.readString();
			e.hasData = in.readBoolean();
			if (e.hasData) {
				e.data = in.readNBT();
			}
			registry.registry[i] = e;
			registry.reversed.put(e.entryId, e);
		}
	}

}
