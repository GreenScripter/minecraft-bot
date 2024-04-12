package greenscripter.minecraft.nbt;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagCompound extends NBTComponent {

	public Map<String, NBTComponent> components = new HashMap<>();

	public byte getType() {
		return TAG_Compound;
	}

	public NBTComponent read(MCInputStream in) throws IOException {
		byte type = in.readByte();
		while (type != TAG_End) {
			String name = in.readUTF();
			components.put(name, NBTComponent.getType(type).read(in));
			type = in.readByte();
		}
		return this;
	}

	public void write(MCOutputStream out) throws IOException {
		for (var e : components.entrySet()) {
			out.writeByte(e.getValue().getType());
			out.writeUTF(e.getKey());
			e.getValue().write(out);
		}
		out.writeByte(TAG_End);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (var e : components.entrySet()) {
			sb.append(e.getKey());
			sb.append(":");
			sb.append(e.getValue().toString());
			sb.append(", ");
		}
		if (sb.length() > 2) {
			sb.setLength(sb.length() - 2);
		}
		sb.append("}");
		return sb.toString();
	}
}
