package greenscripter.minecraft.nbt;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagList<T extends NBTComponent> extends NBTComponent {

	public byte listType;
	public List<T> value = new ArrayList<>();

	public byte getType() {
		return TAG_List;
	}

	@SuppressWarnings("unchecked")
	public NBTComponent read(MCInputStream in) throws IOException {
		listType = in.readByte();
		int length = in.readInt();
		for (int i = 0; i < length; i++) {
			value.add((T) NBTComponent.getType(listType).read(in));
		}
		return this;
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeByte(listType);
		out.writeInt(value.size());
		for (NBTComponent c : value) {
			c.write(out);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (var e : value) {
			sb.append(e.toString());
			sb.append(", ");
		}
		if (sb.length() > 2) {
			sb.setLength(sb.length() - 2);
		}
		sb.append("]");
		return sb.toString();
	}
}
