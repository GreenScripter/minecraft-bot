package greenscripter.minecraft.play.statemachine;

import java.util.ArrayList;
import java.util.List;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.utils.Vector;
import greenscripter.remoteindicators.IndicatorServer;
import greenscripter.statemachine.StateTickCallback;

public class PathFollowState extends PlayerState {

	public List<Vector> followPath;
	public StateTickCallback<ServerConnection> travelFailed;
	public StateTickCallback<ServerConnection> travelComplete;
	public IndicatorServer render;
	public List<Integer> pathIds = new ArrayList<>();
	public int index = 0;
	public Vector last;

	public PathFollowState(List<Vector> followPath) {
		this.followPath = followPath;
		onTick(e -> {
			PositionData pos = e.value.getData(PositionData.class);
			if (last != null && !pos.pos.equals(last)) {
				if (travelFailed != null) travelFailed.tick(e);
				e.pop();
			}
			if (followPath == null) {
				if (travelFailed != null) travelFailed.tick(e);
				e.pop();
			}
			if (index >= followPath.size()) {
				if (travelComplete != null) travelComplete.tick(e);
				e.pop();
			}
			if (render != null && index > 2 && index < pathIds.size()) render.removeShape(pathIds.get(index - 3));

			Vector target = followPath.get(index).copy();
			if ((index & 0xF) > 1) {
				target.y += 0.05;
			}
			pos.setPosRotation(e.value, target, pos.pitch, pos.yaw);
			last = pos.pos.copy();
			index++;
		});
		onFinished(e -> {
			if (render != null) for (int id : pathIds) {
				render.removeShape(id);
			}
		});

	}

}
