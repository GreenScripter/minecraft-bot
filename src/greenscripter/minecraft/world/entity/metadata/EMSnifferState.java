package greenscripter.minecraft.world.entity.metadata;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMSnifferState extends EntityMetadata {

	public int value;

	public int id() {
		return 25;
	}

	public void read(MCInputStream in) throws IOException {
		value = in.readVarInt();
	}

	public static final int IDLING = 0;
	public static final int FEELING_HAPPY = 1;
	public static final int SCENTING = 2;
	public static final int SNIFFING = 3;
	public static final int SEARCHING = 4;
	public static final int DIGGING = 5;
	public static final int RISING = 6;
}
