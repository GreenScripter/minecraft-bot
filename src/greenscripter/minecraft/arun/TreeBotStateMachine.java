package greenscripter.minecraft.arun;

import java.util.Comparator;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.arun.TreeBot.Tree;
import greenscripter.minecraft.arun.TreeBot.TreeBotGlobalData;
import greenscripter.minecraft.arun.TreeBot.TreeBotLocalData;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.statemachine.BreakBlockState;
import greenscripter.minecraft.play.statemachine.PathfindState;
import greenscripter.minecraft.play.statemachine.PlayerMachine;
import greenscripter.minecraft.play.statemachine.PlayerState;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.utils.Vector;
import greenscripter.remoteindicators.IndicatorServer;

public class TreeBotStateMachine extends PlayerMachine {

	public TreeBotStateMachine(ServerConnection t) {
		super(t);
		setState(new FindTreeState());
	}

}

class FindTreeState extends PlayerState {

	public FindTreeState() {
		onTick(e -> {
			PositionData pos = e.value.getData(PositionData.class);
			TreeBotGlobalData global = e.value.getData(TreeBotGlobalData.class);
			var close = global.trees.stream().limit(500).min(Comparator.comparingDouble(t -> pos.pos.squaredDistanceTo(t.blocks.get(0).x, t.blocks.get(0).y, t.blocks.get(0).z)));
			if (close.isPresent()) {
				Tree tree = close.get();
				TreeBot.updateBox(tree.renderId, tree.boundingBox, tree.dimension, IndicatorServer.getColor(255, 255, 0, 255));
				global.trees.remove(tree);
				tree.blocks.sort(Comparator.comparingInt(p -> -p.y));
				e.push(new BreakTreeState(tree));
			}
		});
	}
}

class BreakTreeState extends PlayerState {

	Position target;

	public BreakTreeState(Tree tree) {
		onTick(e -> {
			WorldData world = e.value.getData(WorldData.class);
			PositionData pos = e.value.getData(PositionData.class);
			TreeBotGlobalData global = e.value.getData(TreeBotGlobalData.class);
			TreeBotLocalData local = e.value.getData(TreeBotLocalData.class);
			if (target != null && pos.getEyePos().distanceTo(new Vector(target)) >= 6) {

				local.pathfinder.world = world.world;
				Position stand = local.pathfinder.findDestinations(target);
				if (stand == null) {
					target = null;
					tick();
					return;
				}
				PathfindState pathfind = new PathfindState(global.pathfinding, local.pathfinder, new Position(pos.pos), stand);
				pathfind.render = TreeBot.render;
				pathfind.noPath = e2 -> {
					target = null;
				};
				e.push(pathfind);
			}

			if (target != null && world.world.getBlock(target) != 0) {
				global.searched.remove(target.getEncoded());
				e.push(new BreakBlockState(target));
			}
			if (tree.blocks.isEmpty()) {
				TreeBot.render.removeShape(tree.renderId);
				e.popNow();
			}

			target = tree.blocks.remove(0);

			
			local.pathfinder.world = world.world;
			Position stand = local.pathfinder.findDestinations(target);
			if (stand == null) {
				target = null;
				tick();
				return;
			}
			PathfindState pathfind = new PathfindState(global.pathfinding, local.pathfinder, new Position(pos.pos), stand);
			pathfind.render = TreeBot.render;
			pathfind.noPath = e2 -> {
				target = null;
			};
			e.pushNow(pathfind);

		});
	}
}