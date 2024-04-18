package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;
import java.time.Instant;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ChatMessagePacket extends Packet {

	public String message;
	public Instant instant = Instant.now();
	public long salt = 0;
	public int signatureCount = 0;
	public int messageCount = 0;
	public byte[] ack = new byte[(int) Math.ceil(20 / 8.0)];

	public ChatMessagePacket() {}

	public ChatMessagePacket(String message) {
		this.message = message;
	}

	public int id() {
		return 0x05;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeString(message);
		out.writeLong(instant.toEpochMilli());
		out.writeLong(salt);

		//no signatures or messages supported;
		if (signatureCount != 0 || messageCount != 0) throw new UnsupportedOperationException("signatureCount, messageCount must be 0, got " + signatureCount + ", " + messageCount);
		out.writeVarInt(signatureCount);
		out.writeVarInt(messageCount);

		out.write(ack);

	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
