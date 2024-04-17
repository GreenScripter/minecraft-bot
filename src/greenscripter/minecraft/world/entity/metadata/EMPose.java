package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMPose extends EntityMetadata {

	public int value;

	public int id() {
		return 20;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readVarInt();
	}

	public static final int STANDING = 0;
	public static final int FALL_FLYING = 1;
	public static final int SLEEPING = 2;
	public static final int SWIMMING = 3;
	public static final int SPIN_ATTACK = 4;
	public static final int SNEAKING = 5;
	public static final int LONG_JUMPING = 6;
	public static final int DYING = 7;
	public static final int CROAKING = 8;
	public static final int USING_TONGUE = 9;
	public static final int SITTING = 10;
	public static final int ROARING = 11;
	public static final int SNIFFING = 12;
	public static final int EMERGING = 13;
	public static final int DIGGING = 14;
}
