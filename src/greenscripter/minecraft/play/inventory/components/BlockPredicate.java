package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class BlockPredicate {

	public BlockSet blockSet;
	public Properties[] properties;
	public NBTComponent nbt;

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeBoolean(blockSet != null);
		if (blockSet != null) {
			blockSet.toBytes(out);
		}
		out.writeBoolean(properties != null);
		if (properties != null) {
			out.writeVarInt(properties.length);
			for (Properties p : properties) {
				p.toBytes(out);
			}
		}
		out.writeBoolean(nbt != null);
		if (nbt != null) {
			out.writeNBT(nbt);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		if (in.readBoolean()) {
			blockSet = new BlockSet();
			blockSet.fromBytes(in);
		}
		if (in.readBoolean()) {
			int length = in.readVarInt();
			properties = new Properties[length];
			for (int i = 0; i < length; i++) {
				properties[i] = new Properties();
				properties[i].fromBytes(in);
			}
		}
		if (in.readBoolean()) {
			nbt = in.readNBT();
		}
	}

	public BlockPredicate copy() {
		BlockPredicate c = new BlockPredicate();
		if (blockSet != null) c.blockSet = blockSet.copy();
		if (properties != null) {
			c.properties = new Properties[properties.length];
			for (int i = 0; i < properties.length; i++) {
				c.properties[i] = properties[i].copy();
			}
		}
		if (nbt != null) c.nbt = nbt.copy();
		return c;
	}

	public static class Properties {

		public String name;
		public String exactValue;
		public String minValue;
		public String maxValue;

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeString(name);
			out.writeBoolean(exactValue != null);
			if (exactValue != null) {
				out.writeString(exactValue);
			} else {
				out.writeString(minValue);
				out.writeString(maxValue);
			}
		}

		public void fromBytes(MCInputStream in) throws IOException {
			name = in.readString();
			if (in.readBoolean()) {
				exactValue = in.readString();
			} else {
				minValue = in.readString();
				maxValue = in.readString();
			}
		}

		public Properties copy() {
			Properties c = new Properties();
			c.name = name;
			c.exactValue = exactValue;
			c.minValue = minValue;
			c.maxValue = maxValue;
			return c;
		}
	}

	public static class BlockSet {

		public String tagName;
		public int[] blockIds;

		public void toBytes(MCOutputStream out) throws IOException {
			if (tagName != null) {
				out.writeVarInt(0);
				out.writeString(tagName);
			} else {
				out.writeVarInt(blockIds.length + 1);
				for (int i : blockIds) {
					out.writeVarInt(i);
				}
			}
		}

		public void fromBytes(MCInputStream in) throws IOException {
			int type = in.readVarInt();
			if (type == 0) {
				tagName = in.readString();
			} else {
				blockIds = new int[type - 1];
				for (int i = 0; i < blockIds.length; i++) {
					blockIds[i] = in.readVarInt();
				}
			}
		}

		public BlockSet copy() {
			BlockSet c = new BlockSet();
			c.tagName = tagName;
			return c;
		}
	}
}
