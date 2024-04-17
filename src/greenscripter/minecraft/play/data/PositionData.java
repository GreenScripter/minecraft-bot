package greenscripter.minecraft.play.data;

import greenscripter.minecraft.utils.Vector;

public class PositionData extends PlayData {

	public Vector pos = new Vector();
	public float pitch;
	public float yaw;

	public String dimension;

	public Vector getEyePos() {
		return pos.copy().add(0, 1.62, 0);
	}

}
