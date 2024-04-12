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
	ByteBuffer storage = ByteBuffer.allocate(1 * 1024 * 1024);

	public int peek() throws IOException {
		if (storage.hasRemaining()) return storage.remaining();
		storage.clear();
		channel.configureBlocking(false);
		int read = channel.read(storage);
		channel.configureBlocking(true);
		storage.flip();
		return read;
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
