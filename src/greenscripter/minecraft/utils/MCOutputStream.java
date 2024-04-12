package greenscripter.minecraft.utils;

import java.util.UUID;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.nbt.NBTTagCompound;
import greenscripter.minecraft.packet.Packet;

public class MCOutputStream extends DataOutputStream {

	public int compressionThreshold = -1;

	public MCOutputStream(OutputStream out) {
		super(out);
	}

	public int writeVarInt(int value) throws IOException {
		if ((value & 0xFFFFFF80) == 0) {
			this.write(value);
			return 1;
		}

		this.write(value & 0x7F | 0x80);
		value >>>= 7;
		if ((value & 0xFFFFFF80) == 0) {
			this.write(value);
			return 2;
		}

		this.write(value & 0x7F | 0x80);
		value >>>= 7;
		if ((value & 0xFFFFFF80) == 0) {
			this.write(value);
			return 3;
		}

		this.write(value & 0x7F | 0x80);
		value >>>= 7;
		if ((value & 0xFFFFFF80) == 0) {
			this.write(value);
			return 4;
		}

		this.write(value & 0x7F | 0x80);
		value >>>= 7;

		this.write(value);
		return 5;
	}

	public static int varIntSize(int value) {
		if ((value & 0xFFFFFF80) == 0) {
			return 1;
		}
		value >>>= 7;
		if ((value & 0xFFFFFF80) == 0) {
			return 2;
		}
		value >>>= 7;
		if ((value & 0xFFFFFF80) == 0) {
			return 3;
		}
		value >>>= 7;
		if ((value & 0xFFFFFF80) == 0) {
			return 4;
		}
		value >>>= 7;
		return 5;
	}

	public int writeString(String value) throws IOException {
		byte[] b = value.getBytes(StandardCharsets.UTF_8);
		int len = writeVarInt(b.length);
		this.write(b);
		return len + b.length;
	}

	public void writeUUID(UUID uuid) throws IOException {
		writeLong(uuid.getMostSignificantBits());
		writeLong(uuid.getLeastSignificantBits());
	}

	public synchronized int writePacket(int id, byte[] data) throws IOException {
		int extra = 0;
		if (compressionThreshold >= 0) {
			extra += varIntSize(0);
		}
		int len = writeVarInt(extra + varIntSize(id) + data.length);
		if (compressionThreshold >= 0) {
			writeVarInt(0);
		}
		len += writeVarInt(id);
		this.write(data);
		return len + data.length;
	}

	public int writePacket(Packet packet) throws IOException {
		var bout = new ByteArrayOutputStream();
		MCOutputStream pout = new MCOutputStream(bout);
		packet.toBytes(pout);
		byte[] data = bout.toByteArray();
		return writePacket(packet.id(), data);
	}

	public void writeNBT(NBTTagCompound nbt) throws IOException {
		NBTComponent.writeNetworkNBT(this, nbt);
	}

	public void writePosition(Position p) throws IOException {
		writeLong(((p.x & 0x3FFFFFF) << 38) | ((p.z & 0x3FFFFFF) << 12) | (p.y & 0xFFF));
	}

	public OutputStream wrapped() {
		return out;
	}
}
