package greenscripter.minecraft.packet.s2c.play.entity;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EntityMetaDataPacket extends Packet {

	public int entityID;
	public EntityMetadata[] meta;

	public EntityMetaDataPacket() {}

	public int id() {
		return 0x56;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		meta = EntityMetadata.readMetadata(meta, in);
	}

}
