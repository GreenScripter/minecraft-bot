package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ExecuteCommandPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:chat_command");

	public String command;

	public ExecuteCommandPacket() {}

	public ExecuteCommandPacket(String command) {
		this.command = command;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeString(command);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		command = in.readString();
	}

}
