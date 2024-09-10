package greenscripter.minecraft.atests;

import java.util.concurrent.ThreadLocalRandom;

import greenscripter.minecraft.world.LongBitInStream;
import greenscripter.minecraft.world.LongBitOutStream;

public class BitEncodeTest {

	static long[] masks = new long[65];
	static {
		long v = 0;
		for (int i = 0; i < 65; i++) {
			masks[i] = v;
			v = v << 1 | 1;
		}
	}

	public static void main(String[] args) {
		int[] data = new int[100];
		byte[] bits = new byte[100];
		for (int i = 0; i < data.length; i++) {
			data[i] = ThreadLocalRandom.current().nextInt();
			bits[i] = (byte) ThreadLocalRandom.current().nextInt(0, 32);
		}

		LongBitOutStream bitOut = new LongBitOutStream();
		for (int i = 0; i < data.length; i++) {
			bitOut.writeBits(data[i], bits[i]);
		}

		long[] longs = bitOut.getLongs();
		for (long l : longs) {
			System.out.println(String.format("%64s", Long.toBinaryString(l)).replace(" ", "0"));
		}

		LongBitInStream bitIn = new LongBitInStream(longs);

		for (int i = 0; i < data.length; i++) {
			int value = bitIn.readBits(bits[i]);
			int expected = (int) (data[i] & masks[bits[i]]);
			if (value != expected) {
				System.out.println(value + " != " + expected + " at " + i);
			}
		}

	}

}
