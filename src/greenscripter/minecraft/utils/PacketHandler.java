package greenscripter.minecraft.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PacketHandler {

	public static void writeVarInt(DataOutputStream out, int value) throws IOException {
		while (true) {
			if ((value & 0xFFFFFF80) == 0) {
				out.write(value);
				return;
			}

			out.write(value & 0x7F | 0x80);
			// Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
			value >>>= 7;
		}
	}

	public static int readVarInt(DataInputStream in) throws IOException {
		int value = 0;
		int bitOffset = 0;
		byte currentByte;
		do {
			if (bitOffset == 35) throw new RuntimeException("VarInt is too big");

			currentByte = (byte) in.read();
			value |= (currentByte & 0b01111111) << bitOffset;

			bitOffset += 7;
		} while ((currentByte & 0b10000000) != 0);

		return value;
	}

	public static String readString(DataInputStream in) throws IOException {
		int i = readVarInt(in);
		byte[] b = new byte[i];
		in.readFully(b);
		return new String(b, StandardCharsets.UTF_8);
	}

	public static void writeString(DataOutputStream out, String value) throws IOException {
		byte[] b = value.getBytes(StandardCharsets.UTF_8);
		writeVarInt(out, b.length);
		out.write(b);
	}

	public static void writeBoolean(DataOutputStream out, boolean value) throws IOException {
		out.write(value ? 1 : 0);
	}

	public static void writeShort(DataOutputStream out, int shrt) throws IOException {
		out.writeShort(shrt);
	}

	public static void writePacket(DataOutputStream out, int id, byte[] data) throws IOException {
		ByteArrayOutputStream packet = new ByteArrayOutputStream();
		writeVarInt(new DataOutputStream(packet), id);
		packet.write(data);
		byte[] allData = packet.toByteArray();
		writeVarInt(out, allData.length);
		out.write(allData);
	}

	public static byte[] readPacket(DataInputStream in) throws IOException {
		int length = readVarInt(in);
		byte[] packet = new byte[length];
		in.readFully(packet);
		return packet;
	}
}
