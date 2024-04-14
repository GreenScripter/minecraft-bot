package greenscripter.minecraft.packet.s2c.play;

import java.io.IOException;

import greenscripter.minecraft.nbt.NBTTagCompound;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Position;

public class BlockEntityDataPacket extends Packet {

	public Position pos;
	public int type;
	public NBTTagCompound nbt;

	public int id() {
		return 0x07;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		pos = in.readPosition();
		type = in.readVarInt();
		nbt = in.readNBT();
	}

}
