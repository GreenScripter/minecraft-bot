package greenscripter.minecraft.packet.s2c.play.entity;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.Entity;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EntityMetaDataPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:set_entity_data");

	public int entityID;
	public EntityMetadata[] meta;

	public EntityMetaDataPacket() {}

	public EntityMetaDataPacket(Entity e) {
		entityID = e.entityId;
		this.meta = e.metadata;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(entityID);
		EntityMetadata.writeMetadata(meta, out);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		meta = EntityMetadata.readMetadata(meta, in);

	}

}
