package greenscripter.minecraft.packet;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class UnknownPacket extends Packet {

	public int id;
	public byte[] data;
	public int offset = 0;
	public int length = 0;
	public boolean compressed;

	public UnknownPacket() {

	}

	public UnknownPacket(int id, byte[] data, int offset, int length, boolean compressed) {
		this.id = id;
		this.data = data;
		this.offset = offset;
		this.length = length;
		this.compressed = compressed;
	}

	public int id() {
		return id;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.write(data, offset, length);
	}

	public void fromBytes(MCInputStream in) throws IOException {}

	public <T extends Packet> T convert(T t) {
		try {
			t.fromBytes(new MCInputStream(new ByteArrayInputStream(data, offset, length)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

}
