package greenscripter.minecraft.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import greenscripter.minecraft.gameinfo.BlockStates;

public class PalettedContainer {

	//	private static boolean[] airFilter = BlockStates.addTagToBlockSet(BlockStates.getBlockSet(), "minecraft:air");

	byte bitsPerEntry;
	PalettedContainer.Type type;
	int singleType;//Type if single value
	int[] palette;//Types if INDIRECT
	long[] data;

	public void readBlocksIntoChunk(Chunk c, int yOffset) {
		LongBitInStream in = new LongBitInStream(data);
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

	public void readBiomesIntoChunk(Chunk c, int yOffset) {
		LongBitInStream in = new LongBitInStream(data);
		switch (type) {
			case DIRECT -> {
				for (int y = yOffset; y < 4 + yOffset; y++) {
					for (int z = 0; z < 4; z++) {
						for (int x = 0; x < 4; x++) {
							c.biomes[y][z][x] = in.readBits(bitsPerEntry);
						}
					}
				}
			}
			case INDIRECT -> {
				for (int y = yOffset; y < 4 + yOffset; y++) {
					for (int z = 0; z < 4; z++) {
						for (int x = 0; x < 4; x++) {
							c.biomes[y][z][x] = palette[in.readBits(bitsPerEntry)];
						}
					}
				}
			}
			case SINGLE_VALUE -> {
				for (int y = yOffset; y < 4 + yOffset; y++) {
					for (int z = 0; z < 4; z++) {
						for (int x = 0; x < 4; x++) {
							c.biomes[y][z][x] = singleType;
						}
					}
				}
			}
			default -> {
				throw new RuntimeException("Invalid PalettedContainer type " + type);
			}

		}
	}

	public void initializeBlocksFromChunk(Chunk c, int yOffset, ChunkSection parent) {
		parent.nonAir = 4096;
		boolean[] blockset = BlockStates.getBlockSet();
		List<Integer> paletteTypes = new ArrayList<>();

		for (int y = yOffset; y < 16 + yOffset; y++) {
			for (int z = 0; z < 16; z++) {
				for (int x = 0; x < 16; x++) {
					int block = c.blocks[y][z][x];

					if (!blockset[block]) {
						paletteTypes.add(block);
						blockset[block] = true;
					}

					// Counting up the non-air blocks incurred a performance penalty, 
					// and the client doesn't seem to even care.
					//					if (!airFilter[block]) {
					//						parent.nonAir++;
					//					}
				}
			}
		}

		if (paletteTypes.size() <= 1) {
			type = Type.SINGLE_VALUE;
			Iterator<Integer> it = paletteTypes.iterator();
			singleType = it.hasNext() ? it.next() : 0;
			bitsPerEntry = 0;
		} else if (32 - Integer.numberOfLeadingZeros(paletteTypes.size() - 1) <= 8) {
			bitsPerEntry = (byte) (32 - Integer.numberOfLeadingZeros(paletteTypes.size() - 1));
			if (bitsPerEntry < 4) bitsPerEntry = 4;
			//			if (bitsPerEntry > 4) bitsPerEntry = 8;
			type = Type.INDIRECT;
			palette = new int[paletteTypes.size()];
			int index = 0;
			for (int block : paletteTypes) {
				palette[index] = block;
				index++;
			}
		} else {
			type = Type.DIRECT;
			bitsPerEntry = 15;
		}

		initializeBlocksFromTypedChunk(c, yOffset);
	}

	private void initializeBlocksFromTypedChunk(Chunk c, int yOffset) {
		switch (type) {
			case DIRECT -> {
				LongBitOutStream out = new LongBitOutStream();

				for (int y = yOffset; y < 16 + yOffset; y++) {
					for (int z = 0; z < 16; z++) {
						for (int x = 0; x < 16; x++) {
							out.writeBits(c.blocks[y][z][x], bitsPerEntry);
						}
					}
				}
				data = out.getLongs();
			}
			case INDIRECT -> {
				LongBitOutStream out = new LongBitOutStream();

				int[] inversePalette = new int[BlockStates.noCollideIds.length];
				for (int i = 0; i < palette.length; i++) {
					inversePalette[palette[i]] = i;
				}

				for (int y = yOffset; y < 16 + yOffset; y++) {
					for (int z = 0; z < 16; z++) {
						for (int x = 0; x < 16; x++) {
							out.writeBits(inversePalette[c.blocks[y][z][x]], bitsPerEntry);
						}
					}
				}
				data = out.getLongs();
			}
			case SINGLE_VALUE -> {
				data = new long[0];
				// no-op
			}
			default -> {
				throw new RuntimeException("Invalid PalettedContainer type " + type);
			}

		}
	}

	public void initializeBiomesFromChunk(Chunk c, int yOffset) {
		boolean[] biomeTypes = new boolean[1024];
		List<Integer> paletteTypes = new ArrayList<>();
		for (int y = yOffset; y < 4 + yOffset; y++) {
			for (int z = 0; z < 4; z++) {
				for (int x = 0; x < 4; x++) {
					if (!biomeTypes[c.biomes[y][z][x]]) {
						paletteTypes.add(c.biomes[y][z][x]);
						biomeTypes[c.biomes[y][z][x]] = true;
					}
				}
			}
		}

		if (paletteTypes.size() <= 1) {
			type = Type.SINGLE_VALUE;
			Iterator<Integer> it = paletteTypes.iterator();
			singleType = it.hasNext() ? it.next() : 0;
			bitsPerEntry = 0;
		} else if (32 - Integer.numberOfLeadingZeros(paletteTypes.size() - 1) <= 3) {
			bitsPerEntry = (byte) (32 - Integer.numberOfLeadingZeros(paletteTypes.size() - 1));
			if (bitsPerEntry < 1) bitsPerEntry = 1;
			type = Type.INDIRECT;
			palette = new int[paletteTypes.size()];
			int index = 0;
			for (int block : paletteTypes) {
				palette[index] = block;
				index++;
			}
		} else {
			type = Type.DIRECT;
			bitsPerEntry = 6;
		}
		initializeBiomesFromTypedChunk(c, yOffset);
	}

	private void initializeBiomesFromTypedChunk(Chunk c, int yOffset) {
		switch (type) {
			case DIRECT -> {
				LongBitOutStream out = new LongBitOutStream();

				for (int y = yOffset; y < 4 + yOffset; y++) {
					for (int z = 0; z < 4; z++) {
						for (int x = 0; x < 4; x++) {
							out.writeBits(c.biomes[y][z][x], bitsPerEntry);
						}
					}
				}
				data = out.getLongs();
			}
			case INDIRECT -> {
				LongBitOutStream out = new LongBitOutStream();
				int[] inversePalette = new int[1024];

				for (int i = 0; i < palette.length; i++) {
					inversePalette[palette[i]] = i;
				}

				for (int y = yOffset; y < 4 + yOffset; y++) {
					for (int z = 0; z < 4; z++) {
						for (int x = 0; x < 4; x++) {
							out.writeBits(inversePalette[c.biomes[y][z][x]], bitsPerEntry);
						}
					}
				}
				data = out.getLongs();
			}
			case SINGLE_VALUE -> {
				data = new long[0];
				// no-op
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