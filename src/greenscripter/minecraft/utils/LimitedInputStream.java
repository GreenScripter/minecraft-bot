package greenscripter.minecraft.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends FilterInputStream {

	public LimitedInputStream(InputStream in, int length) {
		super(in);
		limit = length;
	}

	int read = 0;
	int limit = 0;

	public int getPos() {
		return read;
	}

	public void skipRemaining() throws IOException {
		if (limit - read > 0) {
			in.skipNBytes(limit - read);
			read = limit;
		}
	}

	public int read() throws IOException {
		if (limit == read) {
			return -1;
		}
		read++;
		return in.read();
	}

	public int read(byte b[], int off, int len) throws IOException {
		if (len > limit - read) {
			len = limit - read;
			if (len == 0) {
				return -1;
			}
		}
		int r = in.read(b, off, len);
		if (r != -1) read += r;
		return r;
	}

	public long skip(long n) throws IOException {
		if (n > limit - read) {
			n = limit - read;
		}
		long r = in.skip(n);
		read += r;
		return r;
	}

	public int available() throws IOException {
		return Math.min(in.available(), limit - read);
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
