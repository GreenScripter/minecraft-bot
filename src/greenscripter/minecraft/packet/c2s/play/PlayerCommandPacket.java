package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PlayerCommandPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:player_command");

	public int entityID = 0;
	public int actionID = 0;
	public int jumpBoost = 0;

	public static final int START_SNEAKING = 0;
	public static final int STOP_SNEAKING = 1;
	public static final int LEAVE_BED = 2;
	public static final int START_SPRINTING = 3;
	public static final int STOP_SPRINTING = 4;
	public static final int START_HORSE_JUMP = 5;
	public static final int STOP_HORSE_JUMP = 6;
	public static final int OPEN_VEHICLE_INVENTORY = 7;
	public static final int START_ELYTRA_FLY = 8;
	/*
	0	Start sneaking
	1	Stop sneaking
	2	Leave bed
	3	Start sprinting
	4	Stop sprinting
	5	Start jump with horse
	6	Stop jump with horse
	7	Open vehicle inventory
	8	Start flying with elytra
	
	 */

	public PlayerCommandPacket() {}

	public PlayerCommandPacket(int action) {
		this.actionID = action;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(entityID);
		out.writeVarInt(actionID);
		out.writeVarInt(jumpBoost);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
