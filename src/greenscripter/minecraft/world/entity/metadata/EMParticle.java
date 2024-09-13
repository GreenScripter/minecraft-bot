package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMParticle extends EntityMetadata {

	public int particleID;
	public int blockstate;
	public float r;
	public float g;
	public float b;
	public float scale;
	public float r2;
	public float g2;
	public float b2;
	public float roll;
	public Slot item;
	public int type;
	public Position blockPos;
	public int entityId;
	public float eyeHeight;
	public int ticks;
	public int ticksDelay;
	public int color;

	public int id() {
		return 17;
	}

	public void read(MCInputStream in) throws IOException {
		particleID = in.readVarInt();
		switch (particleID) {
			case 1, 2, 28, 105 -> {
				blockstate = in.readVarInt();//blockstate
			}
			case 13 -> {
				r = in.readFloat();//r
				g = in.readFloat();//g
				b = in.readFloat();//b
				scale = in.readFloat();//scale
			}
			case 14 -> {
				r = in.readFloat();//r
				g = in.readFloat();//g
				b = in.readFloat();//b
				r2 = in.readFloat();//r
				g2 = in.readFloat();//g
				b2 = in.readFloat();//b
				scale = in.readFloat();//scale
			}
			case 20 -> {
				color = in.readInt();//color
			}
			case 35 -> {
				roll = in.readFloat();//roll
			}
			case 44 -> {
				item = in.readSlot();//item
			}
			case 45 -> {
				type = in.readVarInt();
				if (type == 0) {//block
					blockPos = in.readPosition();//block pos
				} else if (type == 1) {//entity
					entityId = in.readVarInt();//entityid
					eyeHeight = in.readFloat();//entity eye height
				}
				ticks = in.readVarInt();//ticks
			}
			case 99 -> {
				ticksDelay = in.readVarInt();//ticks delay
			}
		}
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeVarInt(particleID);
		switch (particleID) {
			case 1, 2, 28, 105 -> {
				out.writeVarInt(blockstate);//blockstate
			}
			case 13 -> {
				out.writeFloat(r);//r
				out.writeFloat(g);//g
				out.writeFloat(b);//b
				out.writeFloat(scale);//scale
			}
			case 14 -> {
				out.writeFloat(r);//r
				out.writeFloat(g);//g
				out.writeFloat(b);//b
				out.writeFloat(r2);//r
				out.writeFloat(g2);//g
				out.writeFloat(b2);//b
				out.writeFloat(scale);//scale
			}
			case 20 ->{
				out.writeInt(color);
			}
			case 35 -> {
				out.writeFloat(roll);//roll
			}
			case 44 -> {
				out.writeSlot(item);//item
			}
			case 45 -> {
				out.writeVarInt(type);
				if (type == 0) {//block
					out.writePosition(blockPos);
				} else if (type == 1) {//entity
					out.writeVarInt(entityId);//entityid
					out.writeFloat(eyeHeight);//entity eye height
				}
				out.writeVarInt(ticks);//ticks
			}
			case 99 -> {
				out.writeVarInt(ticksDelay);//ticks delay
			}
		}
	}

	public String toString() {
		return "EMParticle [particleID=" + particleID + ", blockstate=" + blockstate + ", r=" + r + ", g=" + g + ", b=" + b + ", scale=" + scale + ", r2=" + r2 + ", g2=" + g2 + ", b2=" + b2 + ", roll=" + roll + ", " + (item != null ? "item=" + item + ", " : "") + "type=" + type + ", " + (blockPos != null ? "blockPos=" + blockPos + ", " : "") + "entityId=" + entityId + ", eyeHeight=" + eyeHeight + ", ticks=" + ticks + ", ticksDelay=" + ticksDelay + "]";
	}

}
