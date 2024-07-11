package greenscripter.minecraft.packet.s2c.play.entity;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PickupItemPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:take_item_entity");

	public int entityID;
	public int collectorEntityID;
	public int count;

	public PickupItemPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityID = in.readVarInt();
		collectorEntityID = in.readVarInt();
		count = in.readVarInt();
	}

}
