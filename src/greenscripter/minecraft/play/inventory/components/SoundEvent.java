package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SoundEvent {

	public int soundIdPlusOne;
	public String name;
	public boolean hasFixedRange;
	public float range;

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(soundIdPlusOne);
		if (soundIdPlusOne == 0) {
			out.writeString(name);
			out.writeBoolean(hasFixedRange);
			if (hasFixedRange) out.writeFloat(range);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		soundIdPlusOne = in.readVarInt();
		if (soundIdPlusOne == 0) {
			name = in.readString();
			hasFixedRange = in.readBoolean();
			if (hasFixedRange) {
				range = in.readFloat();
			}
		}
	}

	public SoundEvent copy() {
		SoundEvent e = new SoundEvent();
		e.soundIdPlusOne = soundIdPlusOne;
		e.name = name;
		e.hasFixedRange = hasFixedRange;
		e.range = range;
		return e;
	}
}