package greenscripter.minecraft.world;

public class LongBitOutStream {

	long[] data;
	int longIndex = 0;
	long atLong = 0;
	int bitsUsed = 0;

	static long[] masks = new long[65];
	static {
		long v = 0;
		for (int i = 0; i < 65; i++) {
			masks[i] = v;
			v = v << 1 | 1;
		}
	}

	public LongBitOutStream() {
		this.data = new long[512];
	}

	public void writeBits(int value, byte bitCount) {
		if (bitsUsed + bitCount > 64) {
			if (longIndex >= data.length) {
				long[] expanded = new long[data.length * 2];
				System.arraycopy(data, 0, expanded, 0, data.length);
				data = expanded;
			}
			data[longIndex] = atLong;
			atLong = 0;
			bitsUsed = 0;
			longIndex++;
		}
		long write = (value & masks[bitCount]);
		write <<= bitsUsed;
		atLong |= write;
		bitsUsed += bitCount;
	}

	public long[] getLongs() {
		long[] result = new long[longIndex + (bitsUsed > 0 ? 1 : 0)];

		System.arraycopy(data, 0, result, 0, longIndex);

		if (bitsUsed > 0) {
			result[result.length - 1] = atLong;
		}
		return result;
	}

}