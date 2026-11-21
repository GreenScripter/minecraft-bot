package com.greenscripter.minecraftbot.utils.data;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.play.Vector;

public class ScrunchedVector {

	public static Vector read(MCInputStream in) throws IOException {
		int lowest = in.readUnsignedByte();
		if (lowest == 0) {
			return new Vector();
		}
		int middle = in.readUnsignedByte();
		long highest = in.readInt() & 0xFFFFFFFFl;
		long buffer = highest << 16 | middle << 8 | lowest;
		long scale = lowest & 3;
		if ((lowest & 4) == 4) {
			scale |= (in.readVarInt() & 4294967295L) << 2;
		}

		return new Vector(unpack(buffer >> 3) * scale, unpack(buffer >> 18) * scale, unpack(buffer >> 33) * scale);
	}

	public static void write(MCOutputStream out, Vector value) throws IOException {
		double x = makeFinite(value.x);
		double y = makeFinite(value.y);
		double z = makeFinite(value.z);
		double absLength = Math.max(Math.abs(x), Math.max(Math.abs(y), Math.abs(z)));
		if (absLength < 3.051944088384301E-5) {
			out.writeByte(0);
			return;
		}
		long scale = (long) Math.ceil(absLength);
		boolean isPartial = (scale & 3L) != scale;
		long markers = isPartial ? scale & 3L | 4L : scale;
		long xn = convertToShortAngle(x / scale) << 3;
		long yn = convertToShortAngle(y / scale) << 18;
		long zn = convertToShortAngle(z / scale) << 33;
		long buffer = markers | xn | yn | zn;
		out.writeByte((byte) buffer);
		out.writeByte((byte) (buffer >> 8));
		out.writeInt((int) (buffer >> 16));
		if (isPartial) {
			out.writeVarInt((int) (scale >> 2));
		}
	}

	private static double makeFinite(final double value) {
		return Double.isNaN(value) ? 0.0 : Math.clamp(value, -1.7179869183E10, 1.7179869183E10);
	}

	private static long convertToShortAngle(final double value) {
		return Math.round((value * 0.5 + 0.5) * 32766.0);
	}

	private static double unpack(final long value) {
		return Math.min(value & 32767L, 32766.0) * 2.0 / 32766.0 - 1.0;
	}
}
