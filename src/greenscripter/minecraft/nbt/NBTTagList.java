package greenscripter.minecraft.nbt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagList<T extends NBTComponent> extends NBTComponent {

	public byte listType;
	public List<T> value = new ArrayList<>();

	public NBTTagList() {

	}

	public NBTTagList(byte listType) {
		this.listType = listType;
	}

	public byte getType() {
		return TAG_List;
	}

	public T get(int i) {
		return value.get(i);
	}
	
	public void add(T t) {
		value.add(t);
	}

	public int size() {
		return value.size();
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

	@SuppressWarnings("unchecked")
	public NBTTagList<T> copy() {
		NBTTagList<T> copy = new NBTTagList<T>();
		if (value != null) {
			copy.listType = listType;
			for (T t : value) {
				copy.value.add((T) t.copy());
			}
		}
		return copy;
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

	public int hashCode() {
		return Objects.hash(listType, value);
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		@SuppressWarnings("rawtypes")
		NBTTagList other = (NBTTagList) obj;
		return listType == other.listType && Objects.equals(value, other.value);
	}
}
