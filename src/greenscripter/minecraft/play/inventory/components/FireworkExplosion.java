package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class FireworkExplosion {

	public int shape;
	public int[] colors;
	public int[] fadeColors;
	public boolean hasTrail;
	public boolean hasTwinkle;

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(shape);
		out.writeVarInt(colors.length);
		for (int i : colors) {
			out.writeInt(i);
		}
		out.writeVarInt(fadeColors.length);
		for (int i : fadeColors) {
			out.writeInt(i);
		}
		out.writeBoolean(hasTrail);
		out.writeBoolean(hasTwinkle);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		shape = in.readVarInt();
		colors = new int[in.readVarInt()];
		for (int i = 0; i < colors.length; i++) {
			colors[i] = in.readInt();
		}
		fadeColors = new int[in.readVarInt()];
		for (int i = 0; i < fadeColors.length; i++) {
			fadeColors[i] = in.readInt();
		}
		hasTrail = in.readBoolean();
		hasTwinkle = in.readBoolean();
	}

	public FireworkExplosion copy() {
		FireworkExplosion e = new FireworkExplosion();
		e.shape = shape;
		e.hasTrail = hasTrail;
		e.hasTwinkle = hasTwinkle;
		e.colors = new int[colors.length];
		System.arraycopy(colors, 0, e.colors, 0, colors.length);
		e.fadeColors = new int[fadeColors.length];
		System.arraycopy(fadeColors, 0, e.fadeColors, 0, fadeColors.length);
		return e;
	}
}