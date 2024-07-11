package greenscripter.minecraft.packet.s2c.play.self;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Position;

public class LoginPlayPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:login");

	public int entityId;
	public boolean isHardcore;
	public List<String> dimensionNames = new ArrayList<>();
	public int maxPlayers;
	public int viewDistance;
	public int simDistance;
	public boolean reducedDebug;
	public boolean enableRespawnScreen;
	public boolean limitedCrafting;
	public int dimensionType;
	public String dimensionName;
	public long seedHash;
	public byte gamemode;
	public byte previousGamemode;
	public boolean isDebug;
	public boolean isFlat;
	public boolean hasDeathLocation;
	public String deathDimension;
	public Position deathLocation;
	public int portalCooldown;
	public boolean enforcesSecureChat;

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityId = in.readInt();
		isHardcore = in.readBoolean();
		int dimensions = in.readVarInt();
		for (int i = 0; i < dimensions; i++) {
			dimensionNames.add(in.readString());
		}
		maxPlayers = in.readVarInt();
		viewDistance = in.readVarInt();
		simDistance = in.readVarInt();
		reducedDebug = in.readBoolean();
		enableRespawnScreen = in.readBoolean();
		limitedCrafting = in.readBoolean();

		dimensionType = in.readVarInt();
		dimensionName = in.readString();
		seedHash = in.readLong();
		gamemode = in.readByte();
		previousGamemode = in.readByte();
		isDebug = in.readBoolean();
		isFlat = in.readBoolean();
		hasDeathLocation = in.readBoolean();
		if (hasDeathLocation) {
			deathDimension = in.readString();
			deathLocation = in.readPosition();
		}
		portalCooldown = in.readVarInt();
		enforcesSecureChat = in.readBoolean();
	}

}
