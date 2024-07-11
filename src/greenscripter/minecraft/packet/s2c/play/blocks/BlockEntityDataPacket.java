package greenscripter.minecraft.packet.s2c.play.blocks;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.nbt.NBTTagCompound;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Position;

public class BlockEntityDataPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:block_entity_data");

	public Position pos;
	public int type;
	public NBTTagCompound nbt;

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		pos = in.readPosition();
		type = in.readVarInt();
		nbt = (NBTTagCompound) in.readNBT();
	}

}
