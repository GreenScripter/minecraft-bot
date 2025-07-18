package greenscripter.minecraft.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class PeekInputStream extends InputStream {

	public PeekInputStream(SocketChannel channel) throws IOException {
		this.channel = channel;
		storage.limit(0);
		selector = Selector.open();
		channel.register(selector, SelectionKey.OP_READ);
	}

	Selector selector;

	SocketChannel channel;
	ByteBuffer storage = ByteBuffer.allocate(3 * 1024 * 1024);
	ByteBuffer temp = ByteBuffer.allocate(3 * 1024 * 1024);

	public int peek() throws IOException {
		if (storage.hasRemaining()) return storage.remaining();
		storage.clear();
		int read = channel.read(storage);
		storage.flip();
		return read;
	}

	private void addToPeek() throws IOException {
		temp.clear();
		if (storage.hasRemaining()) temp.put(storage);

		channel.configureBlocking(false);
		channel.read(temp);
		//		channel.configureBlocking(true);

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

	private byte[] single = new byte[1];

	public int read() throws IOException {
		if (storage.hasRemaining()) {
			return storage.get() & 0xFF;
		}
		blockingRead(single, 0, 1);
		return single[0];
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
		return blockingRead(b, off + exists, len - exists) + exists;
	}

	private int blockingRead(byte b[], int off, int len) throws IOException {
		int toRead = len;
		int exists = 0;
		while (len > 0 && exists == 0) {
			selector.select();
			peek();
			if (storage.hasRemaining()) {
				exists = Math.min(storage.remaining(), len);
				storage.get(b, off, exists);
				off += exists;
				len -= exists;
			}
		}
		return toRead;
	}

	public long skip(long n) throws IOException {
		int extra = 0;
		if (storage.hasRemaining()) {
			extra = (int) Math.min(storage.remaining(), n);
			storage.position(storage.position() + extra);
		}
		long r = blockingSkip(n - extra);
		return r + extra;
	}

	private long blockingSkip(long n) throws IOException {
		selector.select();
		peek();
		ByteBuffer buf = ByteBuffer.allocate((int) Math.max(n, Integer.MAX_VALUE - 100));
		int skipped = channel.read(buf);
		return skipped;
	}

	public int available() throws IOException {
		if (storage.hasRemaining()) {
			return storage.remaining();
		}
		return peek();
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
