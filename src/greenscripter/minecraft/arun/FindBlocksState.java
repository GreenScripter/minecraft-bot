package greenscripter.minecraft.arun;

import java.util.Comparator;

import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.statemachine.PlayerState;
import greenscripter.minecraft.utils.Position;
import greenscripter.remoteindicators.IndicatorServer;

public class FindBlocksState extends PlayerState {

	public FindBlocksState(boolean[] targets, boolean tunneling) {
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
					e.push(new BreakBlocksState(tree, tunneling));
				}
			}
		});
	}
}