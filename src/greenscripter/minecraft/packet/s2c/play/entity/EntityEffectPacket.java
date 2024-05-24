package greenscripter.minecraft.packet.s2c.play.entity;

import java.io.IOException;

import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class EntityEffectPacket extends Packet {

	public int entityId;
	public int effectId;
	public byte amplifier;
	public int duration;
	public byte flags;
	public NBTComponent factorCodec;

	public static final byte FLAG_IS_AMBIENT = 0x01;
	public static final byte FLAG_SHOW_PARTICLES = 0x02;
	public static final byte FLAG_SHOW_ICON = 0x04;

	public EntityEffectPacket() {}

	public int id() {
		return 0x72;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityId = in.readVarInt();
		effectId = in.readVarInt();
		amplifier = in.readByte();
		duration = in.readVarInt();
		flags = in.readByte();
		if (in.readBoolean()) {
			factorCodec = in.readNBT();
		}
	}

}
