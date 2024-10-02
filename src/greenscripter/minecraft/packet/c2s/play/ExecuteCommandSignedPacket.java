package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;
import java.time.Instant;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ExecuteCommandSignedPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:chat_command_signed");

	public String command;
	public Instant instant = Instant.now();
	public long salt = 0;
	public int signatureCount = 0;
	public int messageCount = 0;
	public byte[] ack = new byte[(int) Math.ceil(20 / 8.0)];

	public ExecuteCommandSignedPacket() {}

	public ExecuteCommandSignedPacket(String command) {
		this.command = command;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeString(command);
		out.writeLong(instant.toEpochMilli());
		out.writeLong(salt);

		//no signatures or messages supported;
		if (signatureCount != 0 || messageCount != 0) throw new UnsupportedOperationException("signatureCount, messageCount must be 0, got " + signatureCount + ", " + messageCount);
		out.writeVarInt(signatureCount);
		out.writeVarInt(messageCount);

		out.write(ack);

	}

	public void fromBytes(MCInputStream in) throws IOException {
		command = in.readString();
		instant = Instant.ofEpochMilli(in.readLong());
		salt = in.readLong();

		//no signatures or messages supported;
	}

}
