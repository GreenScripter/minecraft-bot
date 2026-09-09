package com.greenscripter.minecraftbot.packet;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public abstract class Packet {

	public abstract int id();

	public abstract void toBytes(MCOutputStream out) throws IOException;

	public abstract void fromBytes(MCInputStream in) throws IOException;
}
