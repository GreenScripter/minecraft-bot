package greenscripter.minecraft.arun;

import java.util.Comparator;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.statemachine.PlayerState;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.world.WorldSearch.SearchResult;
import greenscripter.remoteindicators.IndicatorServer;
import greenscripter.statemachine.StateTickPredicate;

public class FindBlocksState extends PlayerState {

	public FindBlocksState(boolean[] targets, boolean tunneling, StateTickPredicate<ServerConnection> done) {
		until(done);
		onTick(e -> {
			PositionData pos = e.value.getData(PositionData.class);
			WorldData world = e.value.getData(WorldData.class);

			var searcher = world.world.worlds.getSearchFor(null, targets, false, true);
			synchronized (searcher.results) {
				var close = searcher.results.stream().filter(t -> t.dimension.equals(world.world.id)).min(Comparator.comparingDouble(t -> pos.pos.squaredDistanceTo(t.blocks.get(0).x, t.blocks.get(0).y, t.blocks.get(0).z)));
				if (close.isPresent()) {
					SearchResult result = close.get();
					GearBot.updateBox(result.renderId, result.boundingBox, result.dimension, IndicatorServer.getColor(255, 255, 0, 255));
					searcher.results.remove(result);
					result.blocks.sort(Comparator.comparingInt((Position p) -> -p.y));
					e.push(new BreakBlocksState(result, tunneling));
				}
			}
		});
	}
}
