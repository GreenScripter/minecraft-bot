package greenscripter.minecraft.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PeekInputStream extends FilterInputStream {

	public PeekInputStream(InputStream in, SocketChannel channel) {
		super(in);//in and channel must go to the same place.
		this.channel = channel;
		storage.limit(0);
	}

	SocketChannel channel;
	ByteBuffer storage = ByteBuffer.allocate(3 * 1024 * 1024);
	ByteBuffer temp = ByteBuffer.allocate(3 * 1024 * 1024);

	public int peek() throws IOException {
		if (storage.hasRemaining()) return storage.remaining();
		storage.clear();
		channel.configureBlocking(false);
		int read = channel.read(storage);
		channel.configureBlocking(true);
		storage.flip();
		return read;
	}

	private void addToPeek() throws IOException {
		temp.clear();
		if (storage.hasRemaining()) temp.put(storage);

		channel.configureBlocking(false);
		channel.read(temp);
		channel.configureBlocking(true);

		ByteBuffer next = storage;
		storage = temp;
		temp = next;

		storage.flip();
	}

	private int peekVarInt() throws IOException {
		if (storage.remaining() < 5) {
			addToPeek();
		}
		int pos = storage.position();

		int value = 0;
		int bitOffset = 0;
		byte currentByte;
		do {
			if (bitOffset == 35) throw new RuntimeException("VarInt is too big");

			currentByte = (byte) storage.get(pos);
			pos++;
			value |= (currentByte & 0b01111111) << bitOffset;

			bitOffset += 7;
		} while ((currentByte & 0b10000000) != 0);

		return value;
	}

	public boolean peekPacket() throws IOException {
		try {
			int size = peekVarInt();
			if (size + MCOutputStream.varIntSize(size) <= storage.remaining()) {
				return true;
			}
			addToPeek();
			return size + MCOutputStream.varIntSize(size) <= storage.remaining();
		} catch (Exception e) {
			return false;
		}
	}

	public int read() throws IOException {
		if (storage.hasRemaining()) {
			return storage.get() & 0xFF;
		}
		return in.read();
	}

	public int read(byte b[], int off, int len) throws IOException {
		int exists = 0;
		if (storage.hasRemaining()) {
			exists = Math.min(storage.remaining(), len);
			storage.get(b, off, exists);
			if (len - exists == 0) {
				return exists;
			}
		}
		//		System.out.println("Forced into blocking read.");
		return in.read(b, off + exists, len - exists) + exists;
	}

	public long skip(long n) throws IOException {
		int extra = 0;
		if (storage.hasRemaining()) {
			extra = (int) Math.min(storage.remaining(), n);
			storage.position(storage.position() + extra);
		}
		long r = in.skip(n - extra);
		return r + extra;
	}

	public int available() throws IOException {
		if (storage.hasRemaining()) {
			return storage.remaining();
		}
		return in.available();
	}

	public void close() throws IOException {}

	public synchronized void mark(int readlimit) {
		throw new UnsupportedOperationException("mark/reset not supported");
	}

	public synchronized void reset() throws IOException {
		throw new IOException("mark/reset not supported");
	}

	public boolean markSupported() {
		return false;
	}
}
