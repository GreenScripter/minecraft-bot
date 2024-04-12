package greenscripter.minecraft.utils;

import java.util.UUID;
import java.util.zip.InflaterInputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.nbt.NBTTagCompound;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.packet.UnknownPacket;

public class MCInputStream extends DataInputStream {

	public boolean compression = false;

	public MCInputStream(InputStream in) {
		super(in);
	}

	public int readVarInt() throws IOException {
		int value = 0;
		int bitOffset = 0;
		byte currentByte;
		do {
			if (bitOffset == 35) throw new RuntimeException("VarInt is too big");

			currentByte = (byte) this.read();
			value |= (currentByte & 0b01111111) << bitOffset;

			bitOffset += 7;
		} while ((currentByte & 0b10000000) != 0);

		return value;
	}

	public String readString() throws IOException {
		int i = readVarInt();
		byte[] b = new byte[i];
		this.readFully(b);
		return new String(b, StandardCharsets.UTF_8);
	}

	public long packetCounter = 0;

	public byte[] readPacket() throws IOException {
		int length = readVarInt();
		int uncompressedLength = 0;
		if (compression) {
			uncompressedLength = readVarInt();
			length -= MCOutputStream.varIntSize(uncompressedLength);
		}
		//		System.out.println(compression + " compression " + length + " uncompressed length " + uncompressedLength);
		byte[] packet = new byte[length];
		this.readFully(packet);
		if (uncompressedLength != 0) {
			packet = ZLib.decompress(packet);
		}
		packetCounter++;
		return packet;
	}

	public UUID readUUID() throws IOException {
		return new UUID(this.readLong(), this.readLong());
	}

	public UnknownPacket readGeneralPacket() throws IOException {
		int length = readVarInt();
		int uncompressedLength = 0;
		if (compression) {
			uncompressedLength = readVarInt();
			length -= MCOutputStream.varIntSize(uncompressedLength);
		}
		byte[] packet;

		if (uncompressedLength != 0) {
			packet = new byte[uncompressedLength];
			LimitedInputStream lis = new LimitedInputStream(this, length);
			@SuppressWarnings("resource")
			InflaterInputStream iis = new InflaterInputStream(lis);

			int read = -1;
			int offset = 0;
			while ((read = iis.read(packet, offset, packet.length - offset)) != -1 && packet.length - offset > 0) {
				offset += read;
			}
			lis.skipRemaining();
		} else {
			packet = new byte[length];
			this.readFully(packet);
		}

		packetCounter++;

		var bin = new ByteIn(packet);
		var in = new MCInputStream(bin);
		int id = in.readVarInt();
		UnknownPacket p = new UnknownPacket(id, packet, bin.getPos(), bin.available());
		return p;
	}

	public static UnknownPacket readGeneralPacketOf(byte[] data) throws IOException {
		var bin = new ByteIn(data);
		var in = new MCInputStream(bin);
		int id = in.readVarInt();
		UnknownPacket p = new UnknownPacket(id, data, bin.getPos(), bin.available());
		return p;
	}

	public static int readPacketID(byte[] data) {
		int index = 0;
		int value = 0;
		int bitOffset = 0;
		byte currentByte;
		do {
			if (bitOffset == 35) throw new RuntimeException("VarInt is too big");

			currentByte = (byte) data[index++];
			value |= (currentByte & 0b01111111) << bitOffset;

			bitOffset += 7;
		} while ((currentByte & 0b10000000) != 0);

		return value;
	}

	public <T extends Packet> T readPacket(T t) throws IOException {
		byte[] data = readPacket();
		var in = new MCInputStream(new ByteArrayInputStream(data));
		int id = in.readVarInt();
		if (id != t.id()) {
			throw new IOException("Packet id mismatch " + t + " " + id + "!=" + t.id());
		}
		t.fromBytes(in);
		return t;
	}

	public NBTTagCompound readNBT() throws IOException {
		return NBTComponent.readNetworkNBT(this);
	}

	public InputStream wrapped() {
		return in;
	}
}
