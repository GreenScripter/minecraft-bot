package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMParticle extends EntityMetadata {

	public int particleID;

	public int id() {
		return 17;
	}

	public void read(MCInputStream in) throws IOException {
		particleID = in.readVarInt();
		switch (particleID) {
			case 2, 3, 27 -> {
				in.readVarInt();//blockstate
			}
			case 14 -> {
				in.readFloat();//r
				in.readFloat();//g
				in.readFloat();//b
				in.readFloat();//scale
			}
			case 15 -> {
				in.readFloat();//r
				in.readFloat();//g
				in.readFloat();//b
				in.readFloat();//scale
				in.readFloat();//r
				in.readFloat();//g
				in.readFloat();//b
			}
			case 33 -> {
				in.readFloat();//roll
			}
			case 42 -> {
				in.readSlot();//item
			}
			case 43 -> {
				int type = in.readVarInt();
				if (type == 0) {//block
					in.readPosition();
				} else if (type == 1) {//entity
					in.readVarInt();//entityid
					in.readFloat();//entity eye height
				}
				in.readVarInt();//ticks
			}
			case 96 -> {
				in.readVarInt();//ticks delay
			}
		}
	}

}
