package greenscripter.minecraft.play.statemachine;

import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.utils.Vector;
import greenscripter.remoteindicators.IndicatorServer;

public class BreakBlockState extends PlayerState {

	public IndicatorServer render;
	int shapeId;

	public BreakBlockState(Position block) {
		this(null, block);
	}

	public BreakBlockState(IndicatorServer render, Position block) {
		this.render = render;
		onInit(e -> {
			WorldData data = e.value.getData(WorldData.class);
			PositionData pos = e.value.getData(PositionData.class);
			if (render != null) shapeId = render.addCuboid(data.world.id, new Vector(block.x, block.y, block.z), new Vector(block.x + 1, block.y + 1, block.z + 1), IndicatorServer.getColor(255, 0, 0, 255));
			data.startBreaking(e.value, block);
			data.finishBreaking(e.value, block);
			if (pos.getEyePos().distanceTo(new Vector(block)) >= 6) {
				e.pop();
			}
		});
		onTick(e -> {
			WorldData data = e.value.getData(WorldData.class);
			PositionData pos = e.value.getData(PositionData.class);

			if (pos.getEyePos().distanceTo(new Vector(block)) >= 6) {
				e.pop();
			}
			//			if (e.value.id == 0) System.out.println(data.world.getBlock(block));
			if (data.finishBreaking(e.value, block)) {
				e.popNow();
			}
		});
		onFinished(e -> {
			if (render != null) {
				render.removeShape(shapeId);
			}
		});

	}

}
