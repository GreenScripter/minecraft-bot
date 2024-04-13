package greenscripter.minecraft.world;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import greenscripter.minecraft.utils.MCInputStream;

public class ChunkDataDecoder {

	public static void decode(Chunk c, byte[] data) throws IOException {
		ChunkStream in = new ChunkStream(new ByteArrayInputStream(data));
		//		boolean anyBig = false;
		for (int i = 0; i < c.height / 16; i++) {
			ChunkSection cs = in.readChunkSection();
			cs.blocks.readBlocksIntoChunk(c, i << 4);
			//			if (cs.blocks.type == Type.DIRECT) {
			//				anyBig = true;
			//			}

		}
		//		if (anyBig) {
		//			var out = new FileOutputStream("chunkSpam.txt");
		//			for (int y = 0; y < c.height; y++) {
		//				for (int z = 0; z < 16; z++) {
		//					for (int x = 0; x < 16; x++) {
		//						int block = c.blocks[y][z][x];
		//						BlockState state = BlockStates.getState(block);
		//						out.write(("setblock ~" + x + " " + (y + c.min_y) + " ~" + z + " " + state.block() + "" + state.properties().toString().replace("{", "[").replace("}", "]")+"\n").getBytes());
		//					}
		//				}
		//			}
		//			out.close();
		//		}
	}

	public static class ChunkStream extends MCInputStream {

		public ChunkStream(InputStream in) {
			super(in);
		}

		public ChunkSection readChunkSection() throws IOException {
			ChunkSection cs = new ChunkSection();
			cs.nonAir = readShort();
			cs.blocks = readPalettedBlockContainer();
			cs.biomes = readPalettedBiomeContainer();
			return cs;
		}

		public PalettedContainer readPalettedBlockContainer() throws IOException {
			PalettedContainer pc = new PalettedContainer();
			pc.bitsPerEntry = readByte();
			if (pc.bitsPerEntry == 0) {
				readSingleValuePalette(pc);
			} else if (pc.bitsPerEntry <= 8) {
				readIndirectPalette(pc);
			} else {
				readDirectPalette(pc);
			}
			pc.data = new long[readVarInt()];
			for (int i = 0; i < pc.data.length; i++) {
				pc.data[i] = readLong();
			}
			return pc;
		}

		public PalettedContainer readPalettedBiomeContainer() throws IOException {
			PalettedContainer pc = new PalettedContainer();
			pc.bitsPerEntry = readByte();
			if (pc.bitsPerEntry == 0) {
				readSingleValuePalette(pc);
			} else if (pc.bitsPerEntry <= 3) {
				readIndirectPalette(pc);
			} else {
				readDirectPalette(pc);
			}
			pc.data = new long[readVarInt()];
			for (int i = 0; i < pc.data.length; i++) {
				pc.data[i] = readLong();
			}
			return pc;
		}

		public void readSingleValuePalette(PalettedContainer c) throws IOException {
			c.type = PalettedContainer.Type.SINGLE_VALUE;
			c.singleType = readVarInt();
		}

		public void readIndirectPalette(PalettedContainer c) throws IOException {
			c.type = PalettedContainer.Type.INDIRECT;
			c.palette = new int[readVarInt()];
			for (int i = 0; i < c.palette.length; i++) {
				c.palette[i] = readVarInt();
			}

		}

		public void readDirectPalette(PalettedContainer c) {
			c.type = PalettedContainer.Type.DIRECT;
			//no-op
		}

	}

	public static class ChunkSection {

		short nonAir;
		PalettedContainer blocks;
		PalettedContainer biomes;

		public String toString() {
			return "ChunkSection blocks: " + nonAir + " block palette " + blocks + " biome palette " + biomes;
		}
	}

	public static class PalettedContainer {

		byte bitsPerEntry;
		Type type;
		int singleType;//Type if single value
		int[] palette;//Types if INDIRECT
		long[] data;

		public void readBlocksIntoChunk(Chunk c, int yOffset) {
			LongBitStream in = new LongBitStream(data);
			switch (type) {
				case DIRECT -> {
					for (int y = yOffset; y < 16 + yOffset; y++) {
						for (int z = 0; z < 16; z++) {
							for (int x = 0; x < 16; x++) {
								c.blocks[y][z][x] = in.readBits(bitsPerEntry);
							}
						}
					}
				}
				case INDIRECT -> {
					for (int y = yOffset; y < 16 + yOffset; y++) {
						for (int z = 0; z < 16; z++) {
							for (int x = 0; x < 16; x++) {
								c.blocks[y][z][x] = palette[in.readBits(bitsPerEntry)];
							}
						}
					}
				}
				case SINGLE_VALUE -> {
					for (int y = yOffset; y < 16 + yOffset; y++) {
						for (int z = 0; z < 16; z++) {
							for (int x = 0; x < 16; x++) {
								c.blocks[y][z][x] = singleType;
							}
						}
					}
				}
				default -> {
					throw new RuntimeException("Invalid PalettedContainer type " + type);
				}

			}
		}

		public static enum Type {
			SINGLE_VALUE, INDIRECT, DIRECT
		}

		public String toString() {
			return "PalettedContainer bits: " + bitsPerEntry + " type: " + type + " single: " + singleType + " palette: " + palette + " datasize: " + data.length;
		}
	}

	public static class LongBitStream {

		long[] data;
		int longIndex = 0;
		long atLong = 0;
		int bitsPeeled = 0;
		static long[] masks = new long[65];
		static {
			long v = 0;
			for (int i = 0; i < 65; i++) {
				masks[i] = v;
				v = v << 1 | 1;
			}
		}

		public LongBitStream(long[] data) {
			this.data = data;
			if (data.length > 0) {
				atLong = data[0];
			}
		}

		public int readBits(byte bitCount) {
			int result = 0;
			if (bitsPeeled + bitCount > 64) {
				bitsPeeled = 0;
				longIndex++;
				atLong = data[longIndex];
			}
			result = (int) (atLong & masks[bitCount]);
			atLong = atLong >> bitCount;
			bitsPeeled += bitCount;
			return result;
		}

	}
}
