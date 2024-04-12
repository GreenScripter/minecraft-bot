package greenscripter.minecraft.packet.c2s.configuration;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class AckFinishConfigPacket extends Packet {


	public AckFinishConfigPacket() {}


	public int id() {
		return 2;
	}

	public void toBytes(MCOutputStream out) throws IOException {
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
