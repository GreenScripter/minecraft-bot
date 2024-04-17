package greenscripter.minecraft.play.data;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.utils.DimensionPosition;

public class PlayerData extends PlayData {

	public int entityId;
	public PositionData pos;
	public WorldData world;

	public Experience experience = new Experience();
	public DimensionPosition deathLocation;

	public float health;
	public int food;
	public float saturation;

	public long lastSwing = System.currentTimeMillis();

	public void init(ServerConnection sc) {
		pos = sc.getData(PositionData.class);
		world = sc.getData(WorldData.class);
	}

	public static class Experience {

		public float progress;
		public int level;
		public int totalXP;
	}

}
