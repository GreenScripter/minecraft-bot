package greenscripter.minecraft.nbt;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public abstract class NBTComponent {
	/*
	0	TAG_End			0	Signifies the end of a TAG_Compound. It is only ever used inside a TAG_Compound, a TAG_List that has it's type id set to TAG_Compound or as the type for a TAG_List if the length is 0 or negative, and is not named even when in a TAG_Compound
	1	TAG_Byte		1	A single signed byte
	2	TAG_Short		2	A single signed, big endian 16 bit integer
	3	TAG_Int			4	A single signed, big endian 32 bit integer
	4	TAG_Long		8	A single signed, big endian 64 bit integer
	5	TAG_Float		4	A single, big endian IEEE-754 single-precision floating point number (NaN possible)
	6	TAG_Double		8	A single, big endian IEEE-754 double-precision floating point number (NaN possible)
	7	TAG_Byte_Array	...	A length-prefixed array of signed bytes. The prefix is a signed integer (thus 4 bytes)
	8	TAG_String		...	A length-prefixed modified UTF-8 string. The prefix is an unsigned short (thus 2 bytes) signifying the length of the string in bytes
	9	TAG_List		...	A list of nameless tags, all of the same type. The list is prefixed with the Type ID of the items it contains (thus 1 byte), and the length of the list as a signed integer (a further 4 bytes). If the length of the list is 0 or negative, the type may be 0 (TAG_End) but otherwise it must be any other type. (The notchian implementation uses TAG_End in that situation, but another reference implementation by Mojang uses 1 instead; parsers should accept any type if the length is <= 0).
	10	TAG_Compound	...	Effectively a list of named tags. Order is not guaranteed.
	11	TAG_Int_Array	...	A length-prefixed array of signed integers. The prefix is a signed integer (thus 4 bytes) and indicates the number of 4 byte integers.
	12	TAG_Long_Array	...	A length-prefixed array of signed longs. The prefix is a signed integer (thus 4 bytes) and indicates the number of 8 byte longs.
	 */

	public abstract byte getType();

	public abstract NBTComponent read(MCInputStream in) throws IOException;

	public abstract void write(MCOutputStream out) throws IOException;

	public boolean isEnd() {
		return getType() == TAG_End;
	}

	public boolean isByte() {
		return getType() == TAG_Byte;
	}

	public boolean isShort() {
		return getType() == TAG_Short;
	}

	public boolean isInt() {
		return getType() == TAG_Int;
	}

	public boolean isLong() {
		return getType() == TAG_Long;
	}

	public boolean isFloat() {
		return getType() == TAG_Float;
	}

	public boolean isDouble() {
		return getType() == TAG_Double;
	}

	public boolean isByteArray() {
		return getType() == TAG_Byte_Array;
	}

	public boolean isString() {
		return getType() == TAG_String;
	}

	public boolean isList() {
		return getType() == TAG_List;
	}

	public boolean isCompound() {
		return getType() == TAG_Compound;
	}

	public boolean isIntArray() {
		return getType() == TAG_Int_Array;
	}

	public boolean isLongArray() {
		return getType() == TAG_Long_Array;
	}

	public NBTTagEnd asEnd() {
		return (NBTTagEnd) this;
	}

	public NBTTagByte asByte() {
		return (NBTTagByte) this;
	}

	public NBTTagShort asShort() {
		return (NBTTagShort) this;
	}

	public NBTTagInt asInt() {
		return (NBTTagInt) this;
	}

	public NBTTagLong asLong() {
		return (NBTTagLong) this;
	}

	public NBTTagFloat asFloat() {
		return (NBTTagFloat) this;
	}

	public NBTTagDouble asDouble() {
		return (NBTTagDouble) this;
	}

	public NBTTagByteArray asByteArray() {
		return (NBTTagByteArray) this;
	}

	public NBTTagString asString() {
		return (NBTTagString) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends NBTComponent> NBTTagList<T> asList() {
		return (NBTTagList<T>) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends NBTComponent> NBTTagList<T> asList(Class<T> c) {
		return (NBTTagList<T>) this;
	}

	public NBTTagCompound asCompound() {
		return (NBTTagCompound) this;
	}

	public NBTTagIntArray asIntArray() {
		return (NBTTagIntArray) this;
	}

	public NBTTagLongArray asLongArray() {
		return (NBTTagLongArray) this;
	}

	public static NBTComponent getType(byte id) {
		switch (id) {
			case TAG_End -> {
				return new NBTTagEnd();
			}
			case TAG_Byte -> {
				return new NBTTagByte();
			}
			case TAG_Short -> {
				return new NBTTagShort();
			}
			case TAG_Int -> {
				return new NBTTagInt();
			}
			case TAG_Long -> {
				return new NBTTagLong();
			}
			case TAG_Float -> {
				return new NBTTagFloat();
			}
			case TAG_Double -> {
				return new NBTTagDouble();
			}
			case TAG_Byte_Array -> {
				return new NBTTagByteArray();
			}
			case TAG_String -> {
				return new NBTTagString();
			}
			case TAG_List -> {
				return new NBTTagList<>();
			}
			case TAG_Compound -> {
				return new NBTTagCompound();
			}
			case TAG_Int_Array -> {
				return new NBTTagIntArray();
			}
			case TAG_Long_Array -> {
				return new NBTTagLongArray();
			}
			default -> {
				throw new RuntimeException("Invalid NBT id " + id);
			}
		}
	}

	public static NBTTagCompound readNBT(MCInputStream in) throws IOException {
		byte id = in.readByte();
		if (id != TAG_Compound) throw new IOException("No wrapper compound on NBT");
		in.readUTF();
		return (NBTTagCompound) getType(id).read(in);
	}

	public static NBTComponent readNetworkNBT(MCInputStream in) throws IOException {
		byte id = in.readByte();
		if (id == 0) return null;
		if (id != TAG_Compound) {
			//appearently chat components can be just a string
			//			System.err.println(id);
			//			throw new IOException("No wrapper compound on NBT");
		}
		return (NBTComponent) getType(id).read(in);
	}

	public static void writeNBT(MCOutputStream out, NBTComponent c) throws IOException {
		out.writeByte(c.getType());
		out.writeUTF("");
		c.write(out);
	}

	public static void writeNetworkNBT(MCOutputStream out, NBTComponent c) throws IOException {
		if (c == null) {
			out.writeByte(0);
			return;
		}
		out.writeByte(c.getType());
		c.write(out);
	}

	public static final byte TAG_End = 0;
	public static final byte TAG_Byte = 1;
	public static final byte TAG_Short = 2;
	public static final byte TAG_Int = 3;
	public static final byte TAG_Long = 4;
	public static final byte TAG_Float = 5;
	public static final byte TAG_Double = 6;
	public static final byte TAG_Byte_Array = 7;
	public static final byte TAG_String = 8;
	public static final byte TAG_List = 9;
	public static final byte TAG_Compound = 10;
	public static final byte TAG_Int_Array = 11;
	public static final byte TAG_Long_Array = 12;
}
