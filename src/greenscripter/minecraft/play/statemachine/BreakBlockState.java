package greenscripter.minecraft.play.statemachine;

import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.utils.Vector;

public class BreakBlockState extends PlayerState {

	public BreakBlockState(Position block) {
		onInit(e -> {
			WorldData data = e.value.getData(WorldData.class);
			PositionData pos = e.value.getData(PositionData.class);

			data.startBreaking(e.value, block);
			data.finishBreaking(e.value, block);
			if (pos.getEyePos().distanceTo(new Vector(block)) >= 5) {
				e.pop();
			}
		});
		onTick(e -> {
			WorldData data = e.value.getData(WorldData.class);
			PositionData pos = e.value.getData(PositionData.class);

			if (pos.getEyePos().distanceTo(new Vector(block)) >= 5) {
				e.pop();
			}
			if (data.finishBreaking(e.value, block)) {
				e.popNow();
			}
		});

	}

}
