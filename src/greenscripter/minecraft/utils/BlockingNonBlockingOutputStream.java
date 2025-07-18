package greenscripter.minecraft.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class BlockingNonBlockingOutputStream extends OutputStream {

	SocketChannel c;

	public BlockingNonBlockingOutputStream(SocketChannel c) throws IOException {
		this.c = c;
		selector = Selector.open();
		c.register(selector, SelectionKey.OP_WRITE);
	}

	ByteBuffer singleton = ByteBuffer.allocate(1);

	Selector selector;

	public void write(int b) throws IOException {
		singleton.clear();
		singleton.put((byte) b);
		singleton.flip();
		while (c.write(singleton) == 0) {
			selector.select();
		}
	}

	public void write(byte[] b, int off, int len) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(b, off, len);
		c.write(buffer);
		while (buffer.hasRemaining()) {
			selector.select();
			c.write(buffer);
		}
	}
}
