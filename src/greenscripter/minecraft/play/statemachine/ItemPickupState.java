package greenscripter.minecraft.play.statemachine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;

import greenscripter.minecraft.gameinfo.Registries;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.utils.BlockBox;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.world.PathFinder;
import greenscripter.minecraft.world.entity.Entity;
import greenscripter.remoteindicators.IndicatorServer;

public class ItemPickupState extends PlayerState {

	public IndicatorServer render;
	int shapeId;
	BlockBox region;
	ExecutorService exec;
	PathFinder finder;

	List<Entity> entities = new ArrayList<>();

	public static final int ITEM_TYPE = Registries.registries.get("minecraft:entity_type").get("minecraft:item");

	public ItemPickupState(ExecutorService exec, PathFinder finder, BlockBox region) {
		this(exec, finder, null, region);
	}

	public ItemPickupState(ExecutorService exec, PathFinder finder, IndicatorServer render, BlockBox region) {
		this.render = render;
		this.exec = exec;
		this.finder = finder;
		onInit(e -> {
			WorldData data = e.value.getData(WorldData.class);
			if (render != null) shapeId = render.addCuboid(data.world.id, region.pos1.corner(), region.pos2.corner().add(1, 1, 1), IndicatorServer.getColor(100, 100, 100, 255));
			data.world.entities.values().forEach(en -> {
				if (en.type == ITEM_TYPE && region.contains(en.pos)) {
					entities.add(en);
				}
			});
			entities.sort(Comparator.comparingInt(en -> en.entityId));
		});
		onTick(e -> {
			WorldData data = e.value.getData(WorldData.class);
			PositionData pos = e.value.getData(PositionData.class);

			if (entities.isEmpty()) e.popNow();

			Entity entity = entities.remove(0);
			while (data.world.getEntity(entity.entityId) == null) {
				if (entities.isEmpty()) e.popNow();
				entity = entities.remove(0);
			}
			Position target = new Position(entity.pos);
			if (!data.world.isPassiblePlayer(target, finder.noCollides)) {
				loop: for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {
						for (int k = -1; k <= 1; k++) {
							Position next = target.copy().add(i, j, k);
							if (data.world.isPassiblePlayer(next, finder.noCollides)) {
								target = next;
								break loop;
							}
						}
					}
				}
			}
			if (data.world.isPassiblePlayer(target, finder.noCollides)) {
				PathfindState pathfind = new PathfindState(exec, finder, new Position(pos.pos), target);
				pathfind.render = render;
				pathfind.then(new PickupWait(entity.entityId));
				e.push(pathfind);
			}
		});
		onFinished(e -> {
			if (render != null) {
				render.removeShape(shapeId);
			}
		});

	}

	class PickupWait extends PlayerState {

		int timeout = 500;

		public PickupWait(int entityId) {
			long start = System.currentTimeMillis();
			onTick(e -> {
				if (System.currentTimeMillis() - start > timeout) {
					e.pop();
				}
				WorldData data = e.value.getData(WorldData.class);

				if (data.world.getEntity(entityId) == null) {
					e.pop();
				}
			});
		}
	}
}
