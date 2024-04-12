package greenscripter.minecraft.nbt;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NBTTagEnd extends NBTComponent {

	public byte getType() {
		return TAG_End;
	}

	public NBTComponent read(MCInputStream in) {
		return this;
	}

	public void write(MCOutputStream out) {}

}
