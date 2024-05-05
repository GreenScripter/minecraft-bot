package greenscripter.minecraft.play.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.BlockStates;
import greenscripter.minecraft.packet.c2s.play.PlayerActionPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionRotationPacket;
import greenscripter.minecraft.play.data.PlayData;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.play.other.PointlessPathfindHandler.PathFindData;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.utils.Vector;
import greenscripter.minecraft.world.PathFinder;

public class SearchPlayHandler extends PlayHandler {

	long start = System.currentTimeMillis();
	boolean[] targets = BlockStates.addToBlockSet(BlockStates.addToBlockSet(BlockStates.getBlockSet(), "minecraft:short_grass"), "minecraft:tall_grass");
	Map<Position, Long> times = new HashMap<>();
	int breakSeq;
	PathFinder finder;
	{
		finder = new PathFinder();
		finder.infiniteVClipAllowed = false;
		PlayData.playData.put(PathFindData.class, PathFindData::new);
		PlayData.playData.put(PositionQueue.class, PositionQueue::new);
		targets = BlockStates.addTagToBlockSet(targets, "minecraft:small_flowers");
		targets = BlockStates.addTagToBlockSet(targets, "minecraft:crops");
		targets = BlockStates.addTagToBlockSet(targets, "minecraft:flowers");
		targets = BlockStates.addTagToBlockSet(targets, "minecraft:fire");
		targets = BlockStates.addTagToBlockSet(targets, "minecraft:saplings");
		targets = BlockStates.addTagToBlockSet(targets, "minecraft:tall_flowers");
		targets = BlockStates.addTagToBlockSet(targets, "minecraft:leaves");
		targets = BlockStates.addToBlockSet(targets, "minecraft:fern");
		targets = BlockStates.addToBlockSet(targets, "minecraft:large_fern");
		targets = BlockStates.addToBlockSet(targets, "minecraft:sugar_cane");
		targets = BlockStates.addToBlockSet(targets, "minecraft:seagrass");
		targets = BlockStates.addToBlockSet(targets, "minecraft:tall_seagrass");
		targets = BlockStates.addToBlockSet(targets, "minecraft:kelp_plant");
		//		targets = BlockStates.removeTagFromBlockSet(targets, "minecraft:leaves");
		//		targets = BlockStates.addToBlockSet(targets, "minecraft:grass_block");
		finder.timeout = 100;
	}

	Set<Position> targetted = new HashSet<>();
	PositionQueue queue = new PositionQueue();

	public void tick(ServerConnection sc) throws IOException {
		if (System.currentTimeMillis() - start < 3000) {
			return;
		}
		WorldData worldData = sc.getData(WorldData.class);
		if (worldData.world == null) {
			return;
		}

		PositionData pos = sc.getData(PositionData.class);
		PathFindData pathData = sc.getData(PathFindData.class);
		//		PositionQueue queue = sc.getState(PositionQueue.class);
		if (pathData.errored) return;

		if (pos.dimension == null) {
			return;
		}
		if (finder.world == null || !finder.world.id.equals(pos.dimension)) {
			finder.world = worldData.world;
		}
		if (!pathData.queue.isEmpty()) {
			PlayerMovePositionRotationPacket p = pathData.queue.remove(0);
			//			System.out.println(pos.x + " " + pos.y + " " + pos.z + " move to " + p.x + " " + p.y + " " + p.z);
			pos.pos.x = p.x;
			pos.pos.y = p.y;
			pos.pos.z = p.z;
			sc.out.writePacket(p);
			return;
		}
		targetted.removeIf(t -> worldData.world.getBlock(t.x, t.y, t.z) <= 0);
		//		System.out.println(worldState.world);
		if (worldData.world != null) {
			Position self = new Position(pos.pos);
			List<Position> blocks = new ArrayList<>();

			if (queue.queue.isEmpty()) {
				blocks = worldData.world.performSearch((int) pos.pos.x, (int) pos.pos.y, (int) pos.pos.z, targets, 200000, 5000, true).stream().filter(p -> !times.containsKey(p) || System.currentTimeMillis() - times.get(p) > 3000).sorted((p1, p2) -> p1.getManhattanDistance(self) - p2.getManhattanDistance(self)).toList();
				queue.queue.addAll(blocks);
				blocks = new ArrayList<>();
			}
			queue.queue.sort((p1, p2) -> p1.getManhattanDistance(self) - p2.getManhattanDistance(self));
			if (!queue.queue.isEmpty()) {
				if (self.getManhattanDistance(queue.queue.get(0)) > 100 && queue.queue.size() < 1000) {
					blocks = worldData.world.performSearch((int) pos.pos.x, (int) pos.pos.y, (int) pos.pos.z, targets, 200000, 50, true).stream().filter(p -> !times.containsKey(p) || System.currentTimeMillis() - times.get(p) > 3000).sorted((p1, p2) -> p1.getManhattanDistance(self) - p2.getManhattanDistance(self)).toList();
					queue.queue.addAll(blocks);
					blocks = new ArrayList<>();
					queue.queue.sort((p1, p2) -> p1.getManhattanDistance(self) - p2.getManhattanDistance(self));
				}
			}

			while (blocks.isEmpty() && !queue.queue.isEmpty()) {
				Position t = queue.queue.remove(0);
				int block = worldData.world.getBlock(t.x, t.y, t.z);

				if (block > 0 && targets[block]) {
					if (!targetted.contains(t)) {
						blocks.add(t);
					}
				}
			}
			if (blocks.isEmpty()) {
				return;
			}

			if (pathData.oldPos != null) {
				sc.out.writePacket(new PlayerActionPacket(PlayerActionPacket.START_MINING, pathData.oldPos, (byte) 1, breakSeq++));
				sc.out.writePacket(new PlayerActionPacket(PlayerActionPacket.FINISH_MINING, pathData.oldPos, (byte) 1, breakSeq++));
				targetted.remove(pathData.oldPos);

				//				sc.out.writePacket(new ExecuteCommandPacket("setblock" + " " + pathState.oldPos.x + " " + (pathState.oldPos.y) + " " + pathState.oldPos.z + " minecraft:air"));

			}
			for (Position p : blocks) {
				pathData.oldPos = p;
				targetted.add(p);
				//				worldState.world.setBlock(p.x, p.y, p.z, 0);
				break;
				//				System.out.println(p);
				//				times.put(p, System.currentTimeMillis());
				//				sc.out.writePacket(new ExecuteCommandPacket("particle minecraft:block_marker " + BlockStates.getState(worldState.world.getBlock(p.x, p.y, p.z)).block() + " " + p.x + " " + (p.y + 0.5) + " " + p.z));
			}
			long start = System.currentTimeMillis();
			List<Vector> path = finder.pathFind(pos.pos.copy(), new Vector(pathData.oldPos), 4);
			if (path == null) {
				if (System.currentTimeMillis() - start > 900) {
					System.out.println(pos.pos + " -> " + pathData.oldPos.x + " " + pathData.oldPos.y + " " + pathData.oldPos.z);
					System.out.println(new Position(pos.pos));
					System.out.println(sc.name);
					//					pathState.errored = true;
				}
				return;
			}
			finder.mergeStraightLines(path, 10);
			pathData.queue.addAll(finder.getPackets(path, pos.pos.copy(), pos.pitch, pos.yaw));
		}
	}

	public boolean handlesTick() {
		return true;
	}

	static class PositionQueue implements PlayData {

		List<Position> queue = new ArrayList<>();
	}
}
