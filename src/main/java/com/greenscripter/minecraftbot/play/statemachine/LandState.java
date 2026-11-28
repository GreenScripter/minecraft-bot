package com.greenscripter.minecraftbot.play.statemachine;

import com.greenscripter.minecraftbot.gameinfo.BlockStates;
import com.greenscripter.minecraftbot.play.data.PositionData;
import com.greenscripter.minecraftbot.play.data.WorldData;
import com.greenscripter.minecraftbot.utils.play.Position;
import com.greenscripter.minecraftbot.utils.play.Vector;

public class LandState extends PlayerState {

	public int maxSpeed = 10;

	public LandState() {
		onInit(e -> {
			var world = e.value.getData(WorldData.class);
			var pos = e.value.getData(PositionData.class);
			var blockpos = new Position(pos.pos);
			if (!world.world.isBlockLoaded((int) blockpos.x, (int) blockpos.z)) {
				return;
			}
			if (!world.world.isPassiblePlayer(new Position(pos.pos).add(0, -1, 0), BlockStates.noCollideIds)) {
				e.popNow();
			}
		});
		onTick(e -> {
			var world = e.value.getData(WorldData.class);
			var pos = e.value.getData(PositionData.class);
			var blockpos = new Position(pos.pos);
			if (!pos.initialized) return;

			if (!world.world.isBlockLoaded((int) blockpos.x, (int) blockpos.z)) {
				return;
			}
			if (!world.world.isPassiblePlayer(new Position(pos.pos).add(0, -1, 0), BlockStates.noCollideIds)) {
				e.popNow();
			}
			var toPos = new Position(pos.pos);
			var movable = toPos;
			for (int i = 0; i < maxSpeed; i++) {
				if (world.world.isPassiblePlayer(toPos.add(0, -1, 0), BlockStates.noCollideIds)) {
					movable = toPos.copy();
				} else {
					break;
				}
			}
			pos.setPos(e.value, new Vector(movable));
			if (!world.world.isPassiblePlayer(new Position(pos.pos).add(0, -1, 0), BlockStates.noCollideIds)) {
				e.pop();
			}
		});
	}

}
