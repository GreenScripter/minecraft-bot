package greenscripter.minecraft.utils;

import java.io.ByteArrayInputStream;

public class ByteIn extends ByteArrayInputStream {

	public ByteIn(byte[] buf) {
		super(buf);
	}

	public ByteIn(byte[] buf, int offset, int length) {
		super(buf, offset, length);
	}

	public byte[] getBuffer() {
		return this.buf;
	}

	public void setBuffer(byte[] b) {
		this.buf = b;
		this.mark = 0;
		this.pos = 0;
		this.count = b.length;
	}
	
	public int getPos() {
		return this.pos;
	}
	
}
