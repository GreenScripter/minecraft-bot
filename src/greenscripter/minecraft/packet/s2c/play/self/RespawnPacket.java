package greenscripter.minecraft.packet.s2c.play.self;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Position;

public class RespawnPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:respawn");

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
	public byte dataKept;

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(dimensionType);
		out.writeString(dimensionName);
		out.writeLong(seedHash);
		out.writeByte(gamemode);
		out.writeByte(previousGamemode);
		out.writeBoolean(isDebug);
		out.writeBoolean(isFlat);
		out.writeBoolean(hasDeathLocation);
		if (hasDeathLocation) {
			out.writeString(deathDimension);
			out.writePosition(deathLocation);
		}
		out.writeVarInt(portalCooldown);
		out.writeByte(dataKept);
	}

	public void fromBytes(MCInputStream in) throws IOException {
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
		dataKept = in.readByte();
	}
	/*
	 * 0x45	Play	Client	
	Dimension Type	Identifier	The type of dimension in the minecraft:dimension_type registry, defined by the Registry Data packet.
	Dimension Name	Identifier	Name of the dimension being spawned into.
	Hashed seed	Long	First 8 bytes of the SHA-256 hash of the world's seed. Used client side for biome noise
	Game mode	Unsigned Byte	0: Survival, 1: Creative, 2: Adventure, 3: Spectator.
	Previous Game mode	Byte	-1: Undefined (null), 0: Survival, 1: Creative, 2: Adventure, 3: Spectator. The previous game mode. Vanilla client uses this for the debug (F3 + N & F3 + F4) game mode switch. (More information needed)
	Is Debug	Boolean	True if the world is a debug mode world; debug mode worlds cannot be modified and have predefined blocks.
	Is Flat	Boolean	True if the world is a superflat world; flat worlds have different void fog and a horizon at y=0 instead of y=63.
	Has death location	Boolean	If true, then the next two fields are present.
	Death dimension Name	Optional Identifier	Name of the dimension the player died in.
	Death location	Optional Position	The location that the player died at.
	Portal cooldown	VarInt	The number of ticks until the player can use the portal again.
	Data kept	Byte	Bit mask. 0x01: Keep attributes, 0x02: Keep metadata. Tells which data should be kept on the client side once the player has respawned.
	In the Notchian implementation, this is context dependent:
	
	normal respawns (after death) keep no data;
	exiting the end poem/credits keeps the attributes;
	other dimension changes (portals or teleports) keep all data.
	
	 */
}
