package greenscripter.minecraft.packet.c2s.play;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class InteractEntityPacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:interact");

	public int entityId;
	public int type;
	public float targetX;
	public float targetY;
	public float targetZ;
	public int hand = HAND_MAIN;
	public boolean sneaking = false;

	public InteractEntityPacket() {

	}

	public InteractEntityPacket(int entityId, int type) {
		this.entityId = entityId;
		this.type = type;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(entityId);
		out.writeVarInt(type);
		if (type == TYPE_INTERACT_AT) {
			out.writeFloat(targetX);
			out.writeFloat(targetY);
			out.writeFloat(targetZ);
			out.writeVarInt(hand);
		}
		if (type == TYPE_INTERACT) {
			out.writeVarInt(hand);
		}
		out.writeBoolean(sneaking);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

	public static final int HAND_MAIN = 0;
	public static final int HAND_OFF = 1;

	public static final int TYPE_INTERACT = 0;
	public static final int TYPE_ATTACK = 1;
	public static final int TYPE_INTERACT_AT = 2;

}
