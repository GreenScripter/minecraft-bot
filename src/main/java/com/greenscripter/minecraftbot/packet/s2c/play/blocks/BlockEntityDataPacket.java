package com.greenscripter.minecraftbot.packet.s2c.play.blocks;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.nbt.NBTTagCompound;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;
import com.greenscripter.minecraftbot.utils.play.Position;

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
