package greenscripter.minecraft.world;

public class PalettedContainer {

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

	public static enum Type {
		SINGLE_VALUE, INDIRECT, DIRECT
	}

	public String toString() {
		return "PalettedContainer bits: " + bitsPerEntry + " type: " + type + " single: " + singleType + " palette: " + palette + " datasize: " + data.length;
	}
}