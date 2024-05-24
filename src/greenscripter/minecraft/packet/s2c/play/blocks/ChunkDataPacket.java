package greenscripter.minecraft.packet.s2c.play.blocks;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.nbt.NBTTagCompound;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.packet.UnknownPacket;
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

	public static int readXCoordinate(UnknownPacket up) throws IOException {
		return (((up.data[0 + up.offset] & 0xFF) << 24) + ((up.data[1 + up.offset] & 0xFF) << 16) + ((up.data[2 + up.offset] & 0xFF) << 8) + ((up.data[3 + up.offset] & 0xFF) << 0));
	}

	public static int readZCoordinate(UnknownPacket up) throws IOException {
		return (((up.data[4 + up.offset] & 0xFF) << 24) + ((up.data[5 + up.offset] & 0xFF) << 16) + ((up.data[6 + up.offset] & 0xFF) << 8) + ((up.data[7 + up.offset] & 0xFF) << 0));
	}

	public void fromBytes(MCInputStream in) throws IOException {
		chunkX = in.readInt();
		chunkZ = in.readInt();
		heightmap = in.readNBT().asCompound();
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
			b.data = (NBTTagCompound) in.readNBT();
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
		public byte xinchunk;
		public byte zinchunk;

		public short y;
		public int type;

		public NBTTagCompound data;

	}
}
