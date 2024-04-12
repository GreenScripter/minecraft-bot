package greenscripter.minecraft.packet;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public abstract class Packet {

	public abstract int id();

	public abstract void toBytes(MCOutputStream out) throws IOException;

	public abstract void fromBytes(MCInputStream in) throws IOException;
}
