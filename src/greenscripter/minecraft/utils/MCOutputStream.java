package greenscripter.minecraft.utils;

import java.util.BitSet;
import java.util.List;
import java.util.UUID;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.play.inventory.Components;
import greenscripter.minecraft.play.inventory.Slot;

public class MCOutputStream extends DataOutputStream {

	public int compressionThreshold = -1;
	public boolean actuallyCompress = false;

	public MCOutputStream(OutputStream out) {
		super(out);
	}

	public void writeVarInt(int value) throws IOException {
		if ((value & 0xFFFFFF80) == 0) {
			this.write(value);
			return;
		}

		this.write(value & 0x7F | 0x80);
		value >>>= 7;
		if ((value & 0xFFFFFF80) == 0) {
			this.write(value);
			return;
		}

		this.write(value & 0x7F | 0x80);
		value >>>= 7;
		if ((value & 0xFFFFFF80) == 0) {
			this.write(value);
			return;
		}

		this.write(value & 0x7F | 0x80);
		value >>>= 7;
		if ((value & 0xFFFFFF80) == 0) {
			this.write(value);
			return;
		}

		this.write(value & 0x7F | 0x80);
		value >>>= 7;

		this.write(value);
		return;
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

	public void writeString(String value) throws IOException {
		byte[] b = value.getBytes(StandardCharsets.UTF_8);
		writeVarInt(b.length);
		this.write(b);
		//		return len + b.length;
	}

	public void writeUUID(UUID uuid) throws IOException {
		writeLong(uuid.getMostSignificantBits());
		writeLong(uuid.getLeastSignificantBits());
	}

	public synchronized void writePacket(int id, byte[] data, boolean flush) throws IOException {
		if (actuallyCompress && compressionThreshold >= 0 && data.length >= compressionThreshold) {
			byte[] compressed = ZLib.compress(id, data);
			int originLength = varIntSize(id) + data.length;
			writeVarInt(compressed.length + varIntSize(originLength));
			writeVarInt(originLength);
			write(compressed);
			if (flush) flush();

		} else {
			int extra = 0;
			if (compressionThreshold >= 0) {
				extra += varIntSize(0);
			}
			writeVarInt(extra + varIntSize(id) + data.length);
			if (compressionThreshold >= 0) {
				writeVarInt(0);
			}
			writeVarInt(id);
			write(data);
			if (flush) flush();
		}
	}

	public void writePacket(Packet packet) throws IOException {
		//		System.out.println("Wrote id " + packet.id() + " " + packet);

		var bout = new ByteArrayOutputStream();
		MCOutputStream pout = new MCOutputStream(bout);
		packet.toBytes(pout);
		byte[] data = bout.toByteArray();
		writePacket(packet.id(), data, true);
	}

	public void writePacketNoFlush(Packet packet) throws IOException {
		//		System.out.println("Wrote id " + packet.id() + " " + packet);

		var bout = new ByteArrayOutputStream();
		MCOutputStream pout = new MCOutputStream(bout);
		packet.toBytes(pout);
		byte[] data = bout.toByteArray();
		writePacket(packet.id(), data, false);
	}

	public void writeNBT(NBTComponent nbt) throws IOException {
		NBTComponent.writeNetworkNBT(this, nbt);
	}

	public void writePosition(Position p) throws IOException {
		writeLong(((p.x & 0x3FFFFFFl) << 38) | ((p.z & 0x3FFFFFFl) << 12) | (p.y & 0xFFFl));
	}

	public void writeVarLong(long value) throws IOException {
		while (true) {
			if ((value & ~((long) 0x7F)) == 0) {
				writeByte((int) value);
				return;
			}

			writeByte((int) ((value & 0x7F) | 0x80));

			// Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
			value >>>= 7;
		}
	}

	public void writeSlot(Slot slot) throws IOException {
		if (!slot.present) {
			writeVarInt(0);
			return;
		}
		writeVarInt(slot.itemCount);
		if (slot.itemCount > 0) {
			writeVarInt(slot.itemId);

			List<Component> added = slot.getComponents().getAddedComponents();
			List<Integer> removed = slot.getComponents().getRemovedComponents();

			writeVarInt(added.size());
			writeVarInt(removed.size());

			for (Component c : added) {
				writeComponent(c);
			}

			for (int i : removed) {
				writeVarInt(i);
			}
		}
	}

	public void writeComponent(Component c) throws IOException {
		Components.writeComponent(this, c);
	}

	public void writeBitSet(BitSet set) throws IOException {
		long[] longs = set.toLongArray();

		writeVarInt(longs.length);
		for (int i = 0; i < longs.length; i++) {
			writeLong(longs[i]);
		}
	}

	public OutputStream wrapped() {
		return out;
	}
}
