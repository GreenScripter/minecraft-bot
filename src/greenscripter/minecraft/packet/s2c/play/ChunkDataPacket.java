package greenscripter.minecraft.packet.s2c.play;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.nbt.NBTTagCompound;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ChunkDataPacket extends Packet {

	public int chunkX;
	public int chunkZ;
	public NBTTagCompound heightmap;
	public byte[] data;

	public List<BlockEntity> blockEntities = new ArrayList<>();

	//lighting not implemented

	public int id() {
		return 0x25;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		chunkX = in.readInt();
		chunkZ = in.readInt();
		heightmap = in.readNBT();
		data = new byte[in.readVarInt()];
		in.readFully(data);
		//		System.out.println(data.length + " bytes for " + chunkX + ", " + chunkZ);
		int count = in.readVarInt();
		//		System.out.println(count + " block entities in chunk");
		for (int i = 0; i < count; i++) {
			BlockEntity b = new BlockEntity();
			int packed_xz = in.readByte() & 0xFF;
			b.xinchunk = (byte) (packed_xz >> 4);
			b.zinchunk = (byte) (packed_xz & 15);
			b.y = in.readShort();
			b.type = in.readVarInt();
			b.data = in.readNBT();
			//			System.out.println((chunkX * 16 + b.xinchunk) + " " + b.y + " " + (chunkZ * 16 + b.zinchunk));
			//			System.out.println(b.type + " " + b.data);
			blockEntities.add(b);
		}

	}

	public static class BlockEntity {

		/*
		  packed_xz = ((blockX & 15) << 4) | (blockZ & 15) // encode
		  x = packed_xz >> 4, z = packed_xz & 15 // decode
		 */
		byte xinchunk;
		byte zinchunk;

		short y;
		int type;

		NBTTagCompound data;

	}
}
