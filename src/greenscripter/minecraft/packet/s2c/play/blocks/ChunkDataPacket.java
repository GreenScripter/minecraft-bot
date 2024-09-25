package greenscripter.minecraft.packet.s2c.play.blocks;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.nbt.NBTTagCompound;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ChunkDataPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:level_chunk_with_light");

	public int chunkX;
	public int chunkZ;
	public NBTTagCompound heightmap;
	public byte[] data;

	public List<BlockEntity> blockEntities = new ArrayList<>();

	//lighting not implemented

	//lighting writing constants
	static byte[] lightSection;
	static BitSet ones;
	static BitSet zeros;

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeInt(chunkX);
		out.writeInt(chunkZ);
		out.writeNBT(heightmap);
		out.writeVarInt(data.length);
		out.write(data);

		out.writeVarInt(blockEntities.size());
		for (BlockEntity b : blockEntities) {
			out.writeByte(b.xinchunk << 4 | b.zinchunk);
			out.writeShort(b.y);
			out.writeVarInt(b.type);
			out.writeNBT(b.data);
		}

		if (ones == null) {
			ones = new BitSet(26);
			ones.set(0, 26, true);
		}

		if (zeros == null) zeros = new BitSet();

		out.writeBitSet(zeros);
		out.writeBitSet(ones);
		out.writeBitSet(zeros);
		out.writeBitSet(zeros);

		out.writeVarInt(26);

		if (lightSection == null) {
			lightSection = new byte[2048];
			for (int i = 0; i < lightSection.length; i++) {
				lightSection[i] = (byte) 0xFF;
			}
		}

		for (int i = 0; i < 26; i++) {
			out.writeVarInt(lightSection.length);
			out.write(lightSection);
		}

		out.writeVarInt(26);

		for (int i = 0; i < 26; i++) {
			out.writeVarInt(lightSection.length);
			out.write(lightSection);
		}
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

		//		// Add code to read light data.
		//
		//		BitSet b1 = in.readBitSet();
		//		BitSet b2 = in.readBitSet();
		//		BitSet b3 = in.readBitSet();
		//		BitSet b4 = in.readBitSet();
		//
		//		int length = in.readVarInt();
		//
		//		for (int i = 0; i < length; i++) {
		//			int sublength = in.readVarInt();
		//			in.readNBytes(sublength);
		//		}
		//
		//		length = in.readVarInt();
		//
		//		for (int i = 0; i < length; i++) {
		//			int sublength = in.readVarInt();
		//			in.readNBytes(sublength);
		//		}
	}

	public void useBlockEntities(Collection<greenscripter.minecraft.world.BlockEntity> use) {
		blockEntities.clear();
		for (var en : use) {
			BlockEntity b = new BlockEntity();
			b.type = en.type;

			b.xinchunk = (byte) (en.pos.x - chunkX * 16);
			b.zinchunk = (byte) (en.pos.z - chunkZ * 16);
			b.y = (short) en.pos.y;
			b.data = en.data;
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
