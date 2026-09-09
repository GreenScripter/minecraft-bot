package com.greenscripter.minecraftbot.play.data;

import com.greenscripter.minecraftbot.ServerConnection;
import com.greenscripter.minecraftbot.utils.DimensionPosition;

public class PlayerData implements PlayData {

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
