package greenscripter.minecraft.packet.s2c.play.self;

import java.io.IOException;

import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class DeathPacket extends Packet {

	public int entityId;
	public NBTComponent message;

	public int id() {
		return 0x3A;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		entityId = in.readVarInt();
		message = in.readNBT();
	}

}
