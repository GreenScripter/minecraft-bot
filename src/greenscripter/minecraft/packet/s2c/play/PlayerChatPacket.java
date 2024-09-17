package greenscripter.minecraft.packet.s2c.play;

import java.util.UUID;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PlayerChatPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:player_chat");

	public String message;
	public UUID sender;

	public PlayerChatPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		sender = in.readUUID();
		in.readVarInt();
		if (in.readBoolean()){
			in.readNBytes(256);
		}
		message = in.readString();
	}

}
