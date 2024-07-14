package greenscripter.minecraft.utils;

import java.util.UUID;
import java.util.zip.InflaterInputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.play.inventory.Components;
import greenscripter.minecraft.play.inventory.Slot;

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

		boolean compressed = false;
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
			compressed = true;
		} else {
			packet = new byte[length];
			this.readFully(packet);
		}

		packetCounter++;

		var bin = new ByteIn(packet);
		var in = new MCInputStream(bin);
		int id = in.readVarInt();
		UnknownPacket p = new UnknownPacket(id, packet, bin.getPos(), bin.available(), compressed);
		return p;
	}

	public static UnknownPacket readGeneralPacketOf(byte[] data) throws IOException {
		var bin = new ByteIn(data);
		var in = new MCInputStream(bin);
		int id = in.readVarInt();
		UnknownPacket p = new UnknownPacket(id, data, bin.getPos(), bin.available(), false);
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

	public NBTComponent readNBT() throws IOException {
		NBTComponent tag = NBTComponent.readNetworkNBT(this);
		return tag;
	}

	public Position readPosition() throws IOException {
		long val = readLong();
		int x = (int) (val >> 38);
		int y = (int) (val << 52 >> 52);
		int z = (int) (val << 26 >> 38);
		return new Position(x, y, z);
	}

	public long readVarLong() throws IOException {
		long value = 0;
		int position = 0;
		byte currentByte;

		while (true) {
			currentByte = readByte();
			value |= (long) (currentByte & 0x7F) << position;

			if ((currentByte & 0x80) == 0) break;

			position += 7;

			if (position >= 64) throw new RuntimeException("VarLong is too big");
		}

		return value;
	}

	public Slot readSlot() throws IOException {
		Slot slot = new Slot();
		slot.itemCount = readVarInt();
		slot.present = slot.itemCount != 0;
		if (slot.present) {
			slot.itemId = readVarInt();
			int componentsToAdd = readVarInt();
			int componentsToRemove = readVarInt();

			//			System.out.println("Reading components of " + ItemId.get(slot.itemId) + " +" + componentsToAdd + " -" + componentsToRemove);
			for (int i = 0; i < componentsToAdd; i++) {
				Component c = readComponent();
				//				System.out.println("Read component " + i + " of " + ItemId.get(slot.itemId) + " " + c);
				slot.getComponents().setComponent(c);
			}

			for (int i = 0; i < componentsToRemove; i++) {
				slot.getComponents().removeComponent(i);
			}
		}
		return slot;
	}

	public Component readComponent() throws IOException {
		return Components.readComponent(this);
	}

	public InputStream wrapped() {
		return in;
	}
}
