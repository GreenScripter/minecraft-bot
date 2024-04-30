package greenscripter.minecraft.arun;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.arun.GearBot.GearBotGlobalData;
import greenscripter.minecraft.arun.GearBot.GearBotLocalData;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.statemachine.BreakBlockState;
import greenscripter.minecraft.play.statemachine.ItemPickupState;
import greenscripter.minecraft.play.statemachine.PathFollowState;
import greenscripter.minecraft.play.statemachine.PlayerMachine;
import greenscripter.minecraft.play.statemachine.PlayerState;
import greenscripter.minecraft.play.statemachine.TunnelState;
import greenscripter.minecraft.play.statemachine.WaitState;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.utils.Vector;
import greenscripter.minecraft.world.WorldSearch.SearchResult;
import greenscripter.remoteindicators.IndicatorServer;

public class GearBotStateMachine extends PlayerMachine {

	public GearBotStateMachine(ServerConnection t) {
		super(t);
		setState(new FindBlocksState(GearBot.ironOre));
	}

}

class FindBlocksState extends PlayerState {

	public FindBlocksState(boolean[] targets) {
		onTick(e -> {
			PositionData pos = e.value.getData(PositionData.class);
			WorldData world = e.value.getData(WorldData.class);

			var searcher = world.world.worlds.getSearchFor(null, targets, false, true);
			synchronized (searcher.results) {
				var close = searcher.results.stream().filter(t -> t.dimension.equals(world.world.id)).min(Comparator.comparingDouble(t -> pos.pos.squaredDistanceTo(t.blocks.get(0).x, t.blocks.get(0).y, t.blocks.get(0).z)));
				if (close.isPresent()) {
					var tree = close.get();
					GearBot.updateBox(tree.renderId, tree.boundingBox, tree.dimension, IndicatorServer.getColor(255, 255, 0, 255));
					searcher.results.remove(tree);
					tree.blocks.sort(Comparator.comparingInt((Position p) -> -p.y));
					e.push(new BreakBlocksState(tree));
				}
			}
		});
	}
}

class BreakBlocksState extends PlayerState {

	Position target;

	public BreakBlocksState(SearchResult tree) {
		this.maxTicksPerTick = 3;
		onInit(e -> {
			WorldData world = e.value.getData(WorldData.class);
			GearBotLocalData local = e.value.getData(GearBotLocalData.class);
			for (var pos : new ArrayList<>(tree.blocks)) {
				int block = world.world.getBlock(pos.x + 1, pos.y, pos.z);
				if (block >= 0 && local.tunneler.sideDanger[block]) {
					tree.blocks.remove(pos);
				}
				block = world.world.getBlock(pos.x - 1, pos.y, pos.z);
				if (block >= 0 && local.tunneler.sideDanger[block]) {
					tree.blocks.remove(pos);
				}
				block = world.world.getBlock(pos.x, pos.y, pos.z + 1);
				if (block >= 0 && local.tunneler.sideDanger[block]) {
					tree.blocks.remove(pos);
				}
				block = world.world.getBlock(pos.x, pos.y, pos.z - 1);
				if (block >= 0 && local.tunneler.sideDanger[block]) {
					tree.blocks.remove(pos);
				}
				block = world.world.getBlock(pos.x, pos.y + 1, pos.z);
				if (block >= 0 && local.tunneler.aboveDanger[block]) {
					tree.blocks.remove(pos);
				}
			}
			if (tree.blocks.isEmpty()) {
				GearBot.render.removeShape(tree.renderId);
				e.pop();
			}
		});
		onTick(e -> {
			WorldData world = e.value.getData(WorldData.class);
			PositionData pos = e.value.getData(PositionData.class);
			GearBotGlobalData global = e.value.getData(GearBotGlobalData.class);
			GearBotLocalData local = e.value.getData(GearBotLocalData.class);
			if (target != null && pos.getEyePos().squaredDistanceTo(new Vector(target)) >= 36) {
				//				System.out.println(pos.getEyePos().distanceTo(new Vector(target))+" "+e.value.name);
				local.tunneler.world = world.world;
				Position stand = local.tunneler.findDestinations(target);
				if (stand == null) {
					target = null;
					tick();
					return;
				}
				TunnelState pathfind = new TunnelState(global.pathfinding, local.tunneler, new Position(pos.pos), stand);
				pathfind.render = GearBot.render;
				pathfind.noPath = e2 -> {
					target = null;
				};
				e.push(pathfind);
			}

			if (target != null && world.world.getBlock(target) != 0) {
				var searcher = world.world.worlds.getSearchFor(null, TreeBot.logs, false, false);

				searcher.foundBlocks.remove(target.getEncoded());
				e.push(new BreakBlockState(GearBot.render, target));
			}
			if (tree.blocks.isEmpty()) {
				GearBot.render.removeShape(tree.renderId);
				e.popNow();
			}

			target = tree.blocks.remove(0);

			local.tunneler.world = world.world;
			List<Vector> land = local.tunneler.land(new Position(pos.pos));
			Vector standLocation = land.isEmpty() ? pos.pos : land.get(land.size() - 1);
			if (standLocation.squaredDistanceTo(new Vector(target).add(0, -1.5, 0)) < 25) {
				if (land.size() == 1) {
					pos.setPosRotation(e.value, land.get(land.size() - 1), pos.pitch, pos.yaw);
				} else if (!land.isEmpty()) {
					e.pushNow(new PathFollowState(land));
				}
				tick();
				return;
			}
			Position stand = local.tunneler.findDestinations(target);
			if (stand == null) {
				target = null;
				tick();
				return;
			}
			TunnelState pathfind = new TunnelState(global.pathfinding, local.tunneler, new Position(pos.pos), stand);
			pathfind.render = GearBot.render;
			pathfind.noPath = e2 -> {
				target = null;
			};
			e.pushNow(pathfind);

		});

		onFinished(e -> {
			GearBotGlobalData global = e.value.getData(GearBotGlobalData.class);
			GearBotLocalData local = e.value.getData(GearBotLocalData.class);
			PlayerState wait = new WaitState(100);
			wait.then(new ItemPickupState(global.pathfinding, local.tunneler, TreeBot.render, tree.boundingBox.expand(5), true));
			e.push(wait);
		});
	}

}