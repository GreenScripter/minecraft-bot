package greenscripter.minecraft.utils;

import java.io.ByteArrayOutputStream;

public class ByteOut extends ByteArrayOutputStream {

	public ByteOut() {
		super();
	}

	public ByteOut(int size) {
		super(size);
	}

	public void clear() {
		this.count = 0;
	}
}
