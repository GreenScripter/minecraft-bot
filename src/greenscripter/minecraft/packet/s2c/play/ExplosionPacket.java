package greenscripter.minecraft.packet.s2c.play;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Position;

public class ExplosionPacket extends Packet {

	public double x;
	public double y;
	public double z;
	public float strength;
	public List<Position> blocks = new ArrayList<>();
	public float vx;
	public float vy;
	public float vz;
	public int blockInteraction;//blockInteraction != 0 means destroy blocks listed
	//discard sound and particle data

	public int id() {
		return 0x1E;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		x = in.readDouble();
		y = in.readDouble();
		z = in.readDouble();
		strength = in.readFloat();
		int length = in.readVarInt();
		int bx = (int) Math.floor(x);
		int by = (int) Math.floor(y);
		int bz = (int) Math.floor(z);
		for (int i = 0; i < length; i++) {
			Position p = new Position(bx, by, bz);
			p.x += in.readByte();
			p.y += in.readByte();
			p.z += in.readByte();
			blocks.add(p);
		}
		vx = in.readFloat();
		vy = in.readFloat();
		vz = in.readFloat();

		blockInteraction = in.readVarInt();

	}

}
