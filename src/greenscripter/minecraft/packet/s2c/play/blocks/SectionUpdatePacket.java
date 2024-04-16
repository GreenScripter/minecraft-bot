package greenscripter.minecraft.packet.s2c.play.blocks;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SectionUpdatePacket extends Packet {

	public int sectionX;
	public int sectionY;
	public int sectionZ;
	public int state;

	public byte[] xs;
	public byte[] ys;
	public byte[] zs;
	public int[] ids;

	public int id() {
		return 0x47;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		long pos = in.readLong();
		sectionX = (int) (pos >> 42);
		sectionY = (int) ((pos << 44) >> 44);
		sectionZ = (int) ((pos << 22) >> 42);

		int length = in.readVarInt();
		xs = new byte[length];
		ys = new byte[length];
		zs = new byte[length];
		ids = new int[length];
		for (int i = 0; i < length; i++) {
			pos = in.readVarLong();
			ids[i] = (int) (pos >> 12);
			xs[i] = (byte) ((pos >> 8) & 0xF);
			ys[i] = (byte) (pos & 0xF);
			zs[i] = (byte) ((pos >> 4) & 0xF);
		}
	}

}
