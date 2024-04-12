package greenscripter.minecraft.packet.s2c.play;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Position;

public class LoginPlayPacket extends Packet {

	public int entityId;
	public boolean isHardcore;
	public List<String> dimensionNames = new ArrayList<>();
	public int maxPlayers;
	public int viewDistance;
	public int simDistance;
	public boolean reducedDebug;
	public boolean enableRespawnScreen;
	public boolean limitedCrafting;
	public String dimensionType;
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

	public int id() {
		return 0x29;
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
		
		
		dimensionType = in.readString();
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
	}
	/*
	  	buf.writeInt(this.playerEntityId);
        buf.writeBoolean(this.hardcore);
        buf.writeCollection(this.dimensionIds, PacketByteBuf::writeRegistryKey);
        buf.writeVarInt(this.maxPlayers);
        buf.writeVarInt(this.viewDistance);
        buf.writeVarInt(this.simulationDistance);
        buf.writeBoolean(this.reducedDebugInfo);
        buf.writeBoolean(this.showDeathScreen);
        buf.writeBoolean(this.doLimitedCrafting);
        this.commonPlayerSpawnInfo.write(buf);
        
        
	 	buf.writeRegistryKey(this.dimensionType);
        buf.writeRegistryKey(this.dimension);
        buf.writeLong(this.seed);
        buf.writeByte(this.gameMode.getId());
        buf.writeByte(GameMode.getId(this.prevGameMode));
        buf.writeBoolean(this.isDebug);
        buf.writeBoolean(this.isFlat);
        buf.writeOptional(this.lastDeathLocation, PacketByteBuf::writeGlobalPos);
        buf.writeVarInt(this.portalCooldown);
	 */
	/*
	Entity ID	Int	The player's Entity ID (EID).
	Is hardcore	Boolean	
	Dimension Count	VarInt	Size of the following array.
	Dimension Names	Array of Identifier	Identifiers for all dimensions on the server.
	Max Players	VarInt	Was once used by the client to draw the player list, but now is ignored.
	View Distance	VarInt	Render distance (2-32).
	Simulation Distance	VarInt	The distance that the client will process specific things, such as entities.
	Reduced Debug Info	Boolean	If true, a Notchian client shows reduced information on the debug screen. For servers in development, this should almost always be false.
	Enable respawn screen	Boolean	Set to false when the doImmediateRespawn gamerule is true.
	Do limited crafting	Boolean	Whether players can only craft recipes they have already unlocked. Currently unused by the client.
	Dimension Type	Identifier	The type of dimension in the minecraft:dimension_type registry, defined by the Registry Data packet.
	Dimension Name	Identifier	Name of the dimension being spawned into.
	Hashed seed	Long	First 8 bytes of the SHA-256 hash of the world's seed. Used client side for biome noise
	Game mode	Unsigned Byte	0: Survival, 1: Creative, 2: Adventure, 3: Spectator.
	Previous Game mode	Byte	-1: Undefined (null), 0: Survival, 1: Creative, 2: Adventure, 3: Spectator. The previous game mode. Vanilla client uses this for the debug (F3 + N & F3 + F4) game mode switch. (More information needed)
	Is Debug	Boolean	True if the world is a debug mode world; debug mode worlds cannot be modified and have predefined blocks.
	Is Flat	Boolean	True if the world is a superflat world; flat worlds have different void fog and a horizon at y=0 instead of y=63.
	Has death location	Boolean	If true, then the next two fields are present.
	Death dimension name	Optional Identifier	Name of the dimension the player died in.
	Death location	Optional Position	The location that the player died at.
	Portal cooldown	VarInt	The number of ticks until the player can use the portal again.
	 */
}
