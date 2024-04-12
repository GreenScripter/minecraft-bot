package greenscripter.minecraft.packet.c2s.configuration;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ClientInfoConfigPacket extends Packet {

	public String locale = "en_US";
	public byte viewDistance = 0;
	public int chatMode = 0;
	public boolean chatColors = true;
	public byte skinParts = (byte) 0xFF;
	public int mainHand = 1;
	public boolean filtering = false;
	public boolean listing = false;

	public ClientInfoConfigPacket() {}

	public int id() {
		return 0;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeString(locale);
		out.writeByte(viewDistance);
		out.writeVarInt(chatMode);
		out.writeBoolean(chatColors);
		out.writeByte(skinParts);
		out.writeVarInt(mainHand);
		out.writeBoolean(filtering);
		out.writeBoolean(listing);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
