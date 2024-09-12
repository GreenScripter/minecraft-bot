package greenscripter.minecraft.packet.s2c.play;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class GameEventPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:game_event");

	public int type;
	public float value;

	public GameEventPacket() {}

	public GameEventPacket(int type) {
		this.type = type;
	}

	public GameEventPacket(int type, float value) {
		this.type = type;
		this.value = value;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(type);
		out.writeFloat(value);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		type = in.readVarInt();
		value = in.readFloat();
	}

	public static final int NO_RESPAWN_BLOCK = 0;
	public static final int BEGIN_RAIN = 1;
	public static final int END_RAIN = 2;
	public static final int CHANGE_GAMEMODE = 3;
	public static final int WIN_GAME = 4;
	public static final int DEMO_EVENT = 5;
	public static final int ARROW_HIT_PLAYER = 6;
	public static final int RAIN_LEVEL_CHANGE = 7;
	public static final int THUNDER_LEVEL_CHANGE = 8;
	public static final int PUFFERFISH_STING_SOUND = 9;
	public static final int ELDER_GUARDIAN_EFFECT = 10;
	public static final int ENABLE_RESPAWN_SCREEN = 11;
	public static final int LIMITED_CRAFTING = 12;
	public static final int START_WAITING_FOR_CHUNKS = 13;

}
