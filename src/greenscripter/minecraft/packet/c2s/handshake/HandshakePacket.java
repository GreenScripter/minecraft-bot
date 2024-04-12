package greenscripter.minecraft.packet.c2s.handshake;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class HandshakePacket extends Packet {

	public int version = 765;
	public String address;
	public int port;
	public int nextState;

	public HandshakePacket() {}

	public HandshakePacket(String address, int port, int nextState) {
		this.address = address;
		this.port = port;
		this.nextState = nextState;
	}

	public int id() {
		return 0;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(version);
		out.writeString(address);
		out.writeShort(port);
		out.writeVarInt(nextState);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		version = in.readVarInt();
		address = in.readString();
		port = in.readUnsignedShort();
		nextState = in.readVarInt();
	}

}
