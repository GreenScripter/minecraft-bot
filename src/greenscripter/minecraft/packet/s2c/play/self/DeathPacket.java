package greenscripter.minecraft.packet.s2c.play.self;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class DeathPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:player_combat_kill");

	public int entityId;
	public NBTComponent message;

	public DeathPacket() {}

	public DeathPacket(int entityId, NBTComponent message) {
		this.entityId = entityId;
		this.message = message;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(entityId);
		out.writeNBT(message);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityId = in.readVarInt();
		message = in.readNBT();
	}

}
