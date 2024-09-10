package greenscripter.minecraft.world;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import greenscripter.minecraft.utils.MCOutputStream;

public class ChunkDataEncoder {

	public static byte[] encode(Chunk c) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		ChunkStream out = new ChunkStream(result);
		for (int i = 0; i < c.height / 16; i++) {
			ChunkSection cs = new ChunkSection();
			cs.biomes = new PalettedContainer();
			cs.biomes.initializeBiomesFromChunk(c, i << 2);
			cs.blocks = new PalettedContainer();
			cs.blocks.initializeBlocksFromChunk(c, i << 4, cs);
			out.writeChunkSection(cs);
		}
		out.close();

		return result.toByteArray();
	}

	public static class ChunkStream extends MCOutputStream {

		public ChunkStream(OutputStream out) {
			super(out);
		}

		public void writeChunkSection(ChunkSection cs) throws IOException {
			writeShort(cs.nonAir);
			writePalettedBlockContainer(cs.blocks);
			writePalettedBiomeContainer(cs.biomes);
		}

		public void writePalettedBlockContainer(PalettedContainer pc) throws IOException {
			writeByte(pc.bitsPerEntry);
			if (pc.bitsPerEntry == 0) {
				writeSingleValuePalette(pc);
			} else if (pc.bitsPerEntry <= 8) {
				writeIndirectPalette(pc);
			} else {
				writeDirectPalette(pc);
			}
			writeVarInt(pc.data.length);
			for (int i = 0; i < pc.data.length; i++) {
				writeLong(pc.data[i]);
			}
		}

		public void writePalettedBiomeContainer(PalettedContainer pc) throws IOException {
			writeByte(pc.bitsPerEntry);
			if (pc.bitsPerEntry == 0) {
				writeSingleValuePalette(pc);
			} else if (pc.bitsPerEntry <= 3) {
				writeIndirectPalette(pc);
			} else {
				writeDirectPalette(pc);
			}
			writeVarInt(pc.data.length);
			for (int i = 0; i < pc.data.length; i++) {
				writeLong(pc.data[i]);
			}
		}

		public void writeSingleValuePalette(PalettedContainer c) throws IOException {
			writeVarInt(c.singleType);
		}

		public void writeIndirectPalette(PalettedContainer c) throws IOException {
			writeVarInt(c.palette.length);
			for (int i = 0; i < c.palette.length; i++) {
				writeVarInt(c.palette[i]);
			}

		}

		public void writeDirectPalette(PalettedContainer c) {
			//no-op
		}
	}
}
