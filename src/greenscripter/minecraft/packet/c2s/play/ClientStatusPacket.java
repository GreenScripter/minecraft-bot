package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ClientStatusPacket extends Packet {

	public int actionID = 0;

	public static final int RESPAWN = 0;
	public static final int STATS = 1;
	/*
	 0	Perform respawn	Sent when the client is ready to complete login and when the client is ready to respawn after death.
	 1	Request stats	Sent when the client opens the Statistics menu.
	 */

	public ClientStatusPacket() {}

	public ClientStatusPacket(int actionID) {
		this.actionID = actionID;
	}

	public int id() {
		return 0x08;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(actionID);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
