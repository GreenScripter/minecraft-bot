package greenscripter.minecraft.world;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import greenscripter.minecraft.utils.MCInputStream;

public class ChunkDataDecoder {

	public static void decode(Chunk c, byte[] data) throws IOException {
		//		long start = System.nanoTime();
		ChunkStream in = new ChunkStream(new ByteArrayInputStream(data));
		//		boolean anyBig = false;
		for (int i = 0; i < c.height / 16; i++) {
			ChunkSection cs = in.readChunkSection();
			cs.blocks.readBlocksIntoChunk(c, i << 4);
			cs.biomes.readBiomesIntoChunk(c, i << 2);
			//			if (cs.blocks.type == Type.DIRECT) {
			//				anyBig = true;
			//			}

		}
		in.close();
		//		System.out.println(System.nanoTime()-start);
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
}
