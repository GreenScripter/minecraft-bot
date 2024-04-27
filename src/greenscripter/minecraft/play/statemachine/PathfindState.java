package greenscripter.minecraft.play.statemachine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.utils.Vector;
import greenscripter.minecraft.world.PathFinder;
import greenscripter.remoteindicators.IndicatorServer;
import greenscripter.statemachine.StateTickCallback;

public class PathfindState extends PlayerState {

	public PathFinder finder;
	public Position start;
	public Position target;
	public List<Vector> followPath;
	public StateTickCallback<ServerConnection> noPath;
	public StateTickCallback<ServerConnection> travelFailed;
	public StateTickCallback<ServerConnection> travelComplete;
	public IndicatorServer render;
	public List<Integer> pathIds = new ArrayList<>();
	ExecutorService exec;

	public PathfindState(ExecutorService exec, PathFinder finder, Position start, Position target) {
		this.finder = finder;
		this.start = start;
		this.target = target;
		this.exec = exec;

		var future = exec.submit(() -> {
			followPath = finder.pathfind(start, target);
		});

		onTick(e -> {
			if (future.isDone()) {
				if (followPath == null) {
					if (noPath != null) noPath.tick(e);
					e.popNow();
				}
				PositionData pos = e.value.getData(PositionData.class);
				followPath = finder.getPacketVectors(followPath, pos.pos);
				if (render != null) if (!followPath.isEmpty()) {
					Vector pathPos = followPath.get(0);
					for (int i = 1; i < followPath.size(); i++) {
						pathIds.add(render.addLine(finder.world.id, pathPos, followPath.get(i), IndicatorServer.getColor(0, 255, 0, 255)));
						pathPos = followPath.get(i);
					}
				}
				e.swapToNow(new PathFollowState());
			}
		});

	}

	public class PathFollowState extends PlayerState {

		int index = 0;
		Vector last;
		boolean triedOver;
		Future<?> repathing;

		public PathFollowState() {
			onTick(e -> {
				PositionData pos = e.value.getData(PositionData.class);
				if (last != null && !pos.pos.equals(last)) {
					if (!triedOver) {
						triedOver = true;
						repathing = exec.submit(() -> {
							followPath = finder.pathfindOver(start, target);
							if (followPath != null) {
								followPath = finder.getPacketVectors(followPath, pos.pos);
							}
							index = 0;
							last = null;
							if (render != null) for (int id : pathIds) {
								render.removeShape(id);
							}
							if (render != null) if (followPath != null && !followPath.isEmpty()) {
								Vector pathPos = followPath.get(0);
								for (int i = 1; i < followPath.size(); i++) {
									pathIds.add(render.addLine(finder.world.id, pathPos, followPath.get(i), IndicatorServer.getColor(0, 255, 0, 255)));
									pathPos = followPath.get(i);
								}
							}
						});

						return;
					} else {
						if (!repathing.isDone()) {
							return;
						}
					}
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

				pos.setPosRotation(e.value, followPath.get(index), pos.pitch, pos.yaw);
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
}
