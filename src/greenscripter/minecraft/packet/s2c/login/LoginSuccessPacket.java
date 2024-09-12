package greenscripter.minecraft.packet.s2c.login;

import java.util.UUID;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class LoginSuccessPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPacketId("login", "minecraft:game_profile");

	public String name;
	public UUID uuid;
	public int properties;

	public LoginSuccessPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeUUID(uuid);
		out.writeString(name);
		out.writeVarInt(properties);
		if (properties != 0) {
			throw new UnsupportedOperationException();
		}
		out.writeBoolean(false);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		uuid = in.readUUID();
		name = in.readString();
		properties = in.readVarInt();
	}

}
