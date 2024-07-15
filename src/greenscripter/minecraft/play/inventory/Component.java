package greenscripter.minecraft.play.inventory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.gson.Gson;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public abstract class Component {

	public abstract int id();

	public abstract void toBytes(MCOutputStream out) throws IOException;

	public abstract void fromBytes(MCInputStream in) throws IOException;

	public abstract Component copy();

	public int hashCode() {
		return id() * 31;
	}

	public boolean equals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other.getClass() != this.getClass()) return false;
		Component c = (Component) other;
		if (c.id() != this.id()) return false;
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		try {
			c.toBytes(new MCOutputStream(array));
		} catch (Exception e) {
			e.printStackTrace();
		}
		CheckingOutputStream check = new CheckingOutputStream(array.toByteArray());
		try {
			this.toBytes(new MCOutputStream(check));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return check.passed;
	}

	private static final Gson gson = new Gson();

	public String toString() {
		return getClass().getSimpleName() + " " + gson.toJson(this);
	}

	private static class CheckingOutputStream extends OutputStream {

		boolean passed = true;
		byte[] data;
		int index = 0;

		public CheckingOutputStream(byte[] data) {
			this.data = data;
		}

		public void write(int b) throws IOException {
			if (!passed) return;
			if (index >= data.length) {
				passed = false;
				return;
			}

			if (data[index] != (byte) b) {
				passed = false;
				return;
			}

			index++;
		}

	}

}
