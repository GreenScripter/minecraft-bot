package greenscripter.minecraft.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import greenscripter.minecraft.gameinfo.BlockStates;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionRotationPacket;
import greenscripter.minecraft.utils.BlockBox;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.utils.Vector;

public class PathFinder {

	public static boolean[] pathFindable = BlockStates.copyBlockSet(BlockStates.noCollideIds);
	static {
		BlockStates.removeFromBlockSet(pathFindable, "minecraft:lava");
		BlockStates.removeFromBlockSet(pathFindable, "minecraft:fire");
		BlockStates.removeFromBlockSet(pathFindable, "minecraft:cobweb");
		BlockStates.removeFromBlockSet(pathFindable, "minecraft:powder_snow");
		BlockStates.removeFromBlockSet(pathFindable, "minecraft:soul_fire");
		BlockStates.removeFromBlockSet(pathFindable, "minecraft:sweet_berry_bush");
	}

	public World world;

	public boolean[] noCollides = pathFindable;

	public int pathRetainDistance = 10;
	public int maxSpeed = 10;
	public int restrictRadius = -1;
	public int longRangeHeight = 322;

	public boolean infiniteVClipAllowed = true;
	public long timeout = 1000;
	private AtomicBoolean running = new AtomicBoolean(false);

	public List<Vector> land(Position start) {
		Position previous = start;
		Position at = start.copy();
		List<Vector> path = new ArrayList<>();
		while (isPassible(at.add(0, -1, 0))) {
			if (at.y < world.min_y) break;
			if (previous.y - at.y >= maxSpeed) {
				previous = at;
				at = at.copy();
				path.add(new Vector(at));
			}
		}
		at.add(0, 1, 0);
		if (!at.equals(start)) path.add(new Vector(at));
		return path;
	}

	public List<Vector> pathFind(Vector start, Vector end, double radius) {
		if (start.copy().multiply(1, 0, 1).distanceTo(end.copy().multiply(1, 0, 1)) > 200) {
			return upAndOver(start, end, radius);
		}
		//		List<Vec3d> beam = getBeamPath(start, end);
		//		if (validate(beam)) {
		//			return beam;
		//		}

		return aStar(start, end, radius);
	}

	public List<Vector> pathFind(Vector start, Position end) {
		if (start.copy().multiply(1, 0, 1).distanceTo(new Vector(end.x + 0.5, end.y, end.z + 0.5).multiply(1, 0, 1)) > 200) {
			return upAndOver(start, new Vector(end.x + 0.5, end.y, end.z + 0.5), 1);
		}
		//		List<Vec3d> beam = getBeamPath(start, new Vec3d(end.x + 0.5, end.y, end.z + 0.5));
		//		if (validate(beam)) {
		//			return beam;
		//		}

		return aStar(start, end);
	}

	public List<Vector> pathFindAny(Vector start, List<Vector> ends, double radius) {
		if (ends.isEmpty()) return null;
		if (start.copy().multiply(1, 0, 1).distanceTo(new Vector(ends.get(0).x + 0.5, ends.get(0).y, ends.get(0).z + 0.5).multiply(1, 0, 1)) > 200) {
			return upAndOver(start, new Vector(ends.get(0).x + 0.5, ends.get(0).y, ends.get(0).z + 0.5), radius);
		}
		//		List<Vec3d> beam = getBeamPath(start, new Vec3d(ends.get(0).x + 0.5, ends.get(0).y, ends.get(0).z + 0.5));
		//		if (validate(beam)) {
		//			return beam;
		//		}

		return aStar(start, ends, radius);
	}

	public List<Vector> upAndOver(Vector start, Vector end, double radius) {
		List<Vector> path = new ArrayList<>();

		List<Vector> part;
		part = pathFind(start, new Vector(start.x, longRangeHeight, start.z), radius);
		if (part == null) {
			//			System.out.println("Failed first part");
			return null;
		}
		path.addAll(part);
		path.addAll(getBeamPath(new Vector(start.x, longRangeHeight, start.z), new Vector(end.x, longRangeHeight, end.z)));
		part = pathFind(new Vector(end.x, longRangeHeight, end.z), end, radius);
		if (part != null) {
			path.addAll(part);
		} else {
			//			System.out.println("Failed second part");
		}

		return path;
	}

	public boolean isRunning() {
		return running.get();
	}

	public void optimizeBroken(List<Vector> path, double distance) {
		distance = distance * distance;
		for (int i = 1; i < path.size() - 1; i++) {

			if (path.get(i - 1).squaredDistanceTo(path.get(i + 1)) < distance /*&& Check cuboid*/) {
				path.remove(i);
				i--;
			}
		}
	}

	public void mergeStraightLines(List<Vector> path, double distance) {
		for (int i = 1; i < path.size() - 1; i++) {
			int same = 0;
			Vector previous = path.get(i - 1);
			Vector current = path.get(i);
			Vector next = path.get(i + 1);
			if (previous.x == current.x && current.x == next.x) {
				same++;
			}
			if (previous.y == current.y && current.y == next.y) {
				same++;
			}
			if (previous.z == current.z && current.z == next.z) {
				same++;
			}
			if (same >= 2 && previous.distanceTo(next) <= distance) {
				path.remove(i);
				i--;
			}
		}
	}

	public List<Vector> pathfind(Position startBlock, Position endBlock) {
		if (new Vector(startBlock).multiply(1, 0, 1).distanceTo(new Vector(endBlock).multiply(1, 0, 1)) > 200) {
			List<Vector> path = upAndOver(new Vector(startBlock), new Vector(endBlock), -1);
			return path;
		}
		var blocks = aStar(startBlock, List.of(), endBlock, -1, null);
		if (blocks == null) return null;
		List<Vector> path = new ArrayList<>();
		for (Position p : blocks) {
			path.add(new Vector(p));
		}
		mergeStraightLines(path, maxSpeed);
		return path;

	}

	public List<Vector> pathfind(Position startBlock, Predicate<Position> endBlock) {
		var blocks = aStar(startBlock, List.of(), null, -1, endBlock);
		if (blocks == null) return null;
		List<Vector> path = new ArrayList<>();
		for (Position p : blocks) {
			path.add(new Vector(p));
		}
		mergeStraightLines(path, maxSpeed);
		return path;

	}

	public List<Vector> pathfindOver(Position startBlock, Position endBlock) {
		return upAndOver(new Vector(startBlock), new Vector(endBlock), -1);
	}

	public List<Position> aStar(Position startBlock, List<Vector> endNear, Position endBlock, double nearLength, Predicate<Position> endCondition) {

		if (startBlock == null || (endBlock == null && endNear.isEmpty() && endCondition == null)) {
			return null;
		}
		if (startBlock.equals(endBlock)) {
			List<Position> path = new ArrayList<>();
			path.add(startBlock);
			return path;
		}
		running.set(true);

		Set<Long> endPoints = new HashSet<>();
		for (Vector pos : endNear) {
			endPoints.add(((long) Math.floor(pos.x) << 32) + (long) Math.floor(pos.z));
		}
		if (endBlock != null)
			endPoints.add(((long) endBlock.x << 32) + endBlock.z);
		else if (!endNear.isEmpty()) endBlock = new Position((int) Math.floor(endNear.get(0).x), (int) Math.floor(endNear.get(0).y), (int) Math.floor(endNear.get(0).z));

		for (int i = 0; i < endNear.size(); i++) {
			Vector end = endNear.get(i);
			if (Math.abs(startBlock.x + 0.5 - end.x) < nearLength && Math.abs(startBlock.y + 0.5 - end.y) < nearLength && Math.abs(startBlock.z + 0.5 - end.z) < nearLength) {
				endBlock = startBlock;
				return new ArrayList<>();
			}
		}

		MaxHeap nodes = new MaxHeap(1000);

		Map<Position, AStarNode> seen = new HashMap<>();
		Set<Position> active = new HashSet<>();

		AStarNode startNode = new AStarNode(startBlock, null, 0);
		nodes.insert(startNode);

		seen.put(startNode.pos, startNode);
		active.add(startNode.pos);

		long start = System.currentTimeMillis();
		BlockBox box = new BlockBox(startBlock, endBlock == null ? startBlock : endBlock).expand(restrictRadius);

		List<AStarNode> next = new ArrayList<>();
		loop: while (nodes.size() > 0) {
			if (System.currentTimeMillis() - start > timeout) {
				return null;
			}
			AStarNode current = nodes.extractMax();
			active.remove(current.pos);
			next.clear();
			if (restrictRadius >= 0 && !box.contains(current.pos)) {
				continue;
			}
			aStarNeighbors(current, next, endPoints);

			for (AStarNode node : next) {
				if (endBlock != null) node.cost += node.pos.getManhattanDistance(endBlock);

				AStarNode otherNode = seen.get(node.pos);
				if (otherNode != null) {
					if (node.compareTo(otherNode) > 0) {
						if (active.add(node.pos)) {
							seen.put(node.pos, node);
							nodes.insert(node);
						} else {
							nodes.updateKey(otherNode, node.cost);
						}
					}
				} else {
					nodes.insert(node);
					seen.put(node.pos, node);
					active.add(node.pos);
					//less need to visit already visited location
					//						if (world.getState(node.pos.x, node.pos.y, node.pos.z) > 1) {
					//							world.setState(node.pos.x, node.pos.y, node.pos.z, (byte) (world.getState(node.pos.x, node.pos.y, node.pos.z) - 1));
					//						}
					if (endBlock != null && node.pos.equals(endBlock)) {
						break loop;
					}
					if (endCondition != null && endCondition.test(node.pos)) {
						endBlock = node.pos.copy();
						break loop;
					}
					for (int i = 0; i < endNear.size(); i++) {
						Vector end = endNear.get(i);
						if (Math.abs(node.pos.x + 0.5 - end.x) < nearLength && Math.abs(node.pos.y + 0.5 - end.y) < nearLength && Math.abs(node.pos.z + 0.5 - end.z) < nearLength) {
							endBlock = node.pos;
							break loop;
						}
					}

				}
			}

		}
		running.set(false);
		if (!active.contains(endBlock)) {
			return null;
		} else {
			AStarNode last = seen.get(endBlock);
			List<Position> blocks = new ArrayList<>();
			while (last != null) {
				//more need to visit previously visited location
				//					if (world.getState(last.pos.x, last.pos.y, last.pos.z) < pathRetainDistance) {
				//						world.setState(last.pos.x, last.pos.y, last.pos.z, (byte) (world.getState(last.pos.x, last.pos.y, last.pos.z) + 3));
				//					}

				blocks.add(last.pos);
				last = last.parent;
			}
			Collections.reverse(blocks);
			return blocks;
		}
	}

	public boolean isPassible(Position p) {
		return world.isPassiblePlayer(p.x, p.y, p.z, noCollides);
	}

	public boolean isPassible(int x, int y, int z) {
		return world.isPassiblePlayer(x, y, z, noCollides);
	}

	public void aStarNeighbors(AStarNode parent, List<AStarNode> blocks, Set<Long> endpoint) {
		Position current = parent.pos;
		double basePriority = 1;
		if (isPassible(current.x + 1, current.y, current.z)) blocks.add(new AStarNode(new Position(current.x + 1, current.y, current.z), parent, -basePriority));
		//		if ((basePriority = world.getState(current.x, current.y + 1, current.z)) >= 1) blocks.add(new AStarNode(new BlockPos(current.x, current.y + 1, current.z), parent, -basePriority));
		if (isPassible(current.x, current.y, current.z + 1)) blocks.add(new AStarNode(new Position(current.x, current.y, current.z + 1), parent, -basePriority));
		if (isPassible(current.x - 1, current.y, current.z)) blocks.add(new AStarNode(new Position(current.x - 1, current.y, current.z), parent, -basePriority));
		//		if ((basePriority = world.getState(current.x, current.y - 1, current.z)) >= 1) blocks.add(new AStarNode(new BlockPos(current.x, current.y - 1, current.z), parent, -basePriority));
		if (isPassible(current.x, current.y, current.z - 1)) blocks.add(new AStarNode(new Position(current.x, current.y, current.z - 1), parent, -basePriority));

		for (int i = -maxSpeed; i <= maxSpeed; i += 1) {
			if (isPassible(current.x, current.y + i, current.z)) {
				basePriority = 1;
				basePriority -= Math.abs(i);
				AStarNode newNode = new AStarNode(new Position(current.x, current.y + i, current.z), parent, -basePriority);
				newNode.extraCost += Math.abs(i);
				if (Math.abs(i) == maxSpeed) {
					newNode.extraCost -= maxSpeed + 1;
				}
				blocks.add(newNode);
			}
		}

		for (int i = 0; i <= maxSpeed; i += 1) {
			if (isPassible(current.x + i, current.y, current.z)) {
				if (Math.abs(i) == maxSpeed) {
					basePriority = 1;
					basePriority -= Math.abs(i);
					AStarNode newNode = new AStarNode(new Position(current.x + i, current.y, current.z), parent, -basePriority);
					newNode.extraCost += Math.abs(i);
					newNode.extraCost -= maxSpeed + 1;

					blocks.add(newNode);
				}
			} else {
				break;
			}
		}
		for (int i = 0; i <= maxSpeed; i += 1) {
			if (isPassible(current.x, current.y, current.z + i)) {
				if (Math.abs(i) == maxSpeed) {
					basePriority = 1;
					basePriority -= Math.abs(i);
					AStarNode newNode = new AStarNode(new Position(current.x, current.y, current.z + i), parent, -basePriority);
					newNode.extraCost += Math.abs(i);
					newNode.extraCost -= maxSpeed + 1;

					blocks.add(newNode);
				}
			} else {
				break;
			}
		}
		for (int i = 0; i <= maxSpeed; i += 1) {
			if (isPassible(current.x - i, current.y, current.z)) {
				if (Math.abs(i) == maxSpeed) {
					basePriority = 1;
					basePriority -= Math.abs(i);
					AStarNode newNode = new AStarNode(new Position(current.x - i, current.y, current.z), parent, -basePriority);
					newNode.extraCost += Math.abs(i);
					newNode.extraCost -= maxSpeed + 1;

					blocks.add(newNode);
				}
			} else {
				break;
			}
		}
		for (int i = 0; i <= maxSpeed; i += 1) {
			if (isPassible(current.x, current.y, current.z - i)) {
				if (Math.abs(i) == maxSpeed) {
					basePriority = 1;
					basePriority -= Math.abs(i);
					AStarNode newNode = new AStarNode(new Position(current.x, current.y, current.z - i), parent, -basePriority);
					newNode.extraCost += Math.abs(i);
					newNode.extraCost -= maxSpeed + 1;

					blocks.add(newNode);
				}
			} else {
				break;
			}
		}
		if (infiniteVClipAllowed && endpoint.contains(((long) current.x << 32) + current.z)) {
			for (int i = -66; i < 323; i += 1) {
				if (isPassible(current.x, i, current.z)) {
					basePriority = 1;
					basePriority -= Math.abs(current.y - i);
					AStarNode newNode = new AStarNode(new Position(current.x, i, current.z), parent, -basePriority);
					newNode.extraCost += Math.abs(current.y - i);
					blocks.add(newNode);
				}
			}
		}

	}

	public List<Vector> aStar(Vector start, Vector end, double radius) {
		Position startBlock = findValidStartNear(start);

		return blockPathToCoordinates(aStar(startBlock, List.of(end), null, radius, null));
	}

	public List<Vector> aStar(Vector start, List<Vector> ends, double radius) {
		//		long startTime = System.nanoTime();
		Position startBlock = findValidStartNear(start);
		//		ChatUtils.message("Took " + (System.nanoTime() - startTime) + " nanoseconds to find valid start");

		//		startTime = System.nanoTime();
		List<Position> aStarResult = aStar(startBlock, ends, null, radius, null);
		//		ChatUtils.message("Took " + (System.nanoTime() - startTime) + " nanoseconds to a*");

		//		startTime = System.nanoTime();
		List<Vector> coordinates = blockPathToCoordinates(aStarResult);
		//		ChatUtils.message("Took " + (System.nanoTime() - startTime) + " nanoseconds to convert to coordinates");

		return coordinates;
	}

	public List<Vector> aStar(Vector start, Position end) {
		Position startBlock = findValidStartNear(start);
		//		System.out.println("Path start at " + startBlock+" original: "+start);
		return blockPathToCoordinates(aStar(startBlock, List.of(), end, -1, null));
	}

	public List<Vector> blockPathToCoordinates(List<Position> blocks) {
		if (blocks == null) return null;
		List<Vector> path = new ArrayList<>();

		for (Position pos : blocks) {
			path.add(new Vector(pos.x + 0.5, pos.y, pos.z + 0.5));
		}

		return path;
	}

	public static class AStarNode implements Comparable<AStarNode> {

		Position pos;
		AStarNode parent;
		double cost;
		double extraCost;
		int heapIndex;

		public AStarNode(Position pos, AStarNode parent, double cost) {
			this.pos = pos;
			this.parent = parent;
			this.cost = cost;
			if (parent != null) extraCost = parent.extraCost + 0.1;
		}

		public int getHeapIndex() {
			return heapIndex;
		}

		public void setHeapIndex(int heapIndex) {
			this.heapIndex = heapIndex;
		}

		public int compareTo(AStarNode o) {
			return Double.compare(o.cost + o.extraCost, cost + extraCost);
		}

	}

	private static class MaxHeap {

		private List<AStarNode> nodes;

		public MaxHeap(int capacity) {
			nodes = new ArrayList<>(capacity);
		}

		//		public MaxHeap(Collection<AStarNode> collection) {
		//			nodes = new ArrayList<>(collection);
		//			for (int i = 0; i < nodes.size(); i++) {
		//				nodes.get(i).setHeapIndex(i);
		//			}
		//			for (int i = size() / 2 - 1; i >= 0; i--) {
		//				maxHeapify(i);
		//			}
		//		}

		public AStarNode getMax() {
			return nodes.get(0);
		}

		public AStarNode extractMax() {
			AStarNode value = getMax();
			AStarNode last = nodes.get(size() - 1);
			last.setHeapIndex(0);
			nodes.set(0, last);
			nodes.remove(size() - 1);
			maxHeapify(0);
			return value;
		}

		public int size() {
			return nodes.size();
		}

		public void insert(AStarNode elt) {
			int index = nodes.size();
			elt.setHeapIndex(index);
			nodes.add(elt);
			bubbleUp(index);
		}

		private void bubbleUp(int index) {
			AStarNode s = nodes.get(index);
			int parent;
			//parent of the root is itself, so loop will terminate at the root.
			while (nodes.get(parent = parent(index)).compareTo(s) < 0) {
				swap(index, parent);
				index = parent;
			}
		}

		public void updateKey(AStarNode elt, double newValue) {
			elt.cost = newValue;

			//Extract the heap index
			int index = elt.getHeapIndex();

			bubbleUp(index);
			maxHeapify(index);
		}

		private int parent(int index) {
			return (index - 1) / 2;
		}

		private int left(int index) {
			return 2 * index + 1;
		}

		private int right(int index) {
			return 2 * index + 2;
		}

		private void swap(int from, int to) {
			AStarNode fromStudent = nodes.get(from);
			fromStudent.setHeapIndex(to);
			AStarNode toStudent = nodes.get(to);
			toStudent.setHeapIndex(from);
			nodes.set(from, toStudent);
			nodes.set(to, fromStudent);
		}

		private void maxHeapify(int index) {
			int left = left(index);
			int right = right(index);
			int largest = index;
			if (left < size() && nodes.get(left).compareTo(nodes.get(largest)) > 0) {
				largest = left;
			}
			if (right < size() && nodes.get(right).compareTo(nodes.get(largest)) > 0) {
				largest = right;
			}
			if (largest != index) {
				swap(index, largest);
				maxHeapify(largest);
			}
		}
	}

	public Position findDestinations(Position target) {
		Position grounded = findDestinations(target, true);
		if (grounded == null) {
			return findDestinations(target, false);
		}
		return grounded;
	}

	public Position findDestinations(Position target, boolean grounded) {
		target = target.copy();
		if (isPassible(target) && (!grounded || !world.isPassible(target.x, target.y - 1, target.z, noCollides))) {
			return target;
		}
		if (isPassible(target.add(0, 1, 0)) && (!grounded || !world.isPassible(target.x, target.y - 1, target.z, noCollides))) {
			return target;
		}
		if (isPassible(target.add(0, -2, 0)) && (!grounded || !world.isPassible(target.x, target.y - 1, target.z, noCollides))) {
			return target;
		}
		if (isPassible(target.add(0, -1, 0)) && (!grounded || !world.isPassible(target.x, target.y - 1, target.z, noCollides))) {
			return target;
		}
		if (isPassible(target.add(0, -1, 0)) && (!grounded || !world.isPassible(target.x, target.y - 1, target.z, noCollides))) {
			return target;
		}
		target.add(0, 3, 0);
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					Position next = target.copy().add(i, j, k);
					if (isPassible(next) && (!grounded || !world.isPassible(next.x, next.y - 1, next.z, noCollides))) {
						return next;
					}
				}
			}
		}
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				for (int k = -2; k <= 2; k++) {
					if (i * i + (j + 1.5) * (j + 1.5) + k * k > 25) continue;
					if (Math.abs(i) <= 1 && Math.abs(j) <= 1 && Math.abs(k) <= 1) continue;
					Position next = target.copy().add(i, j, k);
					if (isPassible(next) && (!grounded || !world.isPassible(next.x, next.y - 1, next.z, noCollides))) {
						return next;
					}
				}
			}
		}
		for (int i = -3; i <= 3; i++) {
			for (int j = -4; j <= 3; j++) {
				for (int k = -3; k <= 3; k++) {
					if (i * i + (j + 1.5) * (j + 1.5) + k * k > 25) continue;
					if (Math.abs(i) <= 2 && Math.abs(j) <= 2 && Math.abs(k) <= 2) continue;
					Position next = target.copy().add(i, j, k);
					if (isPassible(next) && (!grounded || !world.isPassible(next.x, next.y - 1, next.z, noCollides))) {
						return next;
					}
				}
			}
		}
		return null;
	}

	public Position findDestinations(Position target, Predicate<Position> p) {
		Position grounded = findDestinations(target, true, p);
		if (grounded == null) {
			return findDestinations(target, false, p);
		}
		return grounded;
	}

	public Position findDestinations(Position target, boolean grounded, Predicate<Position> p) {
		target = target.copy();
		if (isPassible(target) && (!grounded || !world.isPassible(target.x, target.y - 1, target.z, noCollides)) && p.test(target)) {
			return target;
		}
		target.add(0, 1, 0);
		if (isPassible(target) && (!grounded || !world.isPassible(target.x, target.y - 1, target.z, noCollides)) && p.test(target)) {
			return target;
		}
		target.add(0, -2, 0);
		if (isPassible(target) && (!grounded || !world.isPassible(target.x, target.y - 1, target.z, noCollides)) && p.test(target)) {
			return target;
		}
		target.add(0, -1, 0);
		if (isPassible(target) && (!grounded || !world.isPassible(target.x, target.y - 1, target.z, noCollides)) && p.test(target)) {
			return target;
		}
		target.add(0, -1, 0);
		if (isPassible(target) && (!grounded || !world.isPassible(target.x, target.y - 1, target.z, noCollides)) && p.test(target)) {
			return target;
		}
		target.add(0, 3, 0);
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					Position next = target.copy().add(i, j, k);
					if (isPassible(next) && (!grounded || !world.isPassible(next.x, next.y - 1, next.z, noCollides)) && p.test(next)) {
						return next;
					}
				}
			}
		}
		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				for (int k = -2; k <= 2; k++) {
					if (i * i + (j + 1.5) * (j + 1.5) + k * k > 25) continue;
					if (Math.abs(i) <= 1 && Math.abs(j) <= 1 && Math.abs(k) <= 1) continue;
					Position next = target.copy().add(i, j, k);
					if (isPassible(next) && (!grounded || !world.isPassible(next.x, next.y - 1, next.z, noCollides)) && p.test(next)) {
						return next;
					}
				}
			}
		}
		for (int i = -3; i <= 3; i++) {
			for (int j = -4; j <= 3; j++) {
				for (int k = -3; k <= 3; k++) {
					if (i * i + (j + 1.5) * (j + 1.5) + k * k > 25) continue;
					if (Math.abs(i) <= 2 && Math.abs(j) <= 2 && Math.abs(k) <= 2) continue;
					Position next = target.copy().add(i, j, k);
					if (isPassible(next) && (!grounded || !world.isPassible(next.x, next.y - 1, next.z, noCollides)) && p.test(next)) {
						return next;
					}
				}
			}
		}
		return null;
	}

	public Position findValidStartNear(Vector start) {
		Position first = new Position((int) Math.floor(start.x), (int) Math.floor(start.y), (int) Math.floor(start.z));
		//		MoveCheckPlayerEntity move = new MoveCheckPlayerEntity();
		double min = Double.POSITIVE_INFINITY;
		Position minPos = null;

		//		int block = world.getBlock(first.x, first.y, first.z);
		//		System.out.println(first+" "+start);
		//		System.out.println(block + " " + BlockStates.getState(block));
		if (isPassible(first.x, first.y, first.z)) {
			return first;
		}

		for (int distance = 0; distance <= 10; distance++) {
			for (int x = -distance; x <= distance; x++) {
				for (int y = -distance; y <= distance; y++) {
					for (int z = -distance; z <= distance; z++) {
						if (Math.abs(x) == distance || Math.abs(y) == distance || Math.abs(z) == distance) {
							if (start.squaredDistanceTo(first.x + x + 0.5, first.y + y, first.z + z + 0.5) <= min) {
								if (isPassible(first.x + x, first.y + y, first.z + z)) {
									//									move.setPosition(start);
									//									if (!move.isWrongMove(new Vector(first.x + x + 0.5, first.y + y, first.z + z + 0.5))) {
									Position pos = new Position(first.x + x, first.y + y, first.z + z);
									min = start.squaredDistanceTo(first.x + x + 0.5, first.y + y, first.z + z + 0.5);
									minPos = pos;
									//									}
								}
							}
						}
					}
				}
			}
		}
		if (minPos == null) {
			for (int y = -66; y < 323; y++) {
				if (start.squaredDistanceTo(first.x, y, first.z) <= min) {
					if (isPassible(first.x, y, first.z)) {
						//						move.setPosition(start);
						//						if (!move.isWrongMove(new Vector(first.x + 0.5, y, first.z + 0.5))) {
						Position pos = new Position(first.x, y, first.z);
						min = start.squaredDistanceTo(first.x + 0.5, y, first.z + 0.5);
						minPos = pos;
						//						}
					}
				}
			}
		}
		return minPos;
	}

	//	public static boolean validateBroken(List<Vector> path) {
	//		if (path == null) return false;
	//		if (path.isEmpty()) return true;
	//		//		MoveCheckPlayerEntity moveCheck = new MoveCheckPlayerEntity();
	//		//		moveCheck.setPosition(path.get(0));
	//		for (Vector v : path) {
	//			//			if (moveCheck.isWrongMove(v)) {
	//			//				return false;
	//			//			}
	//			//			moveCheck.setPosition(v);
	//		}
	//		return true;
	//	}
	//
	//	public static boolean validateBroken(List<Vector> path, Vector startPos) {
	//		if (path == null) return false;
	//		if (path.isEmpty()) return true;
	//		//		MoveCheckPlayerEntity moveCheck = new MoveCheckPlayerEntity();
	//		//		moveCheck.setPosition(startPos);
	//		for (Vector v : path) {
	//			//			if (moveCheck.isWrongMove(v)) {
	//			//				return false;
	//			//			}
	//			//			moveCheck.setPosition(v);
	//		}
	//		return true;
	//	}

	public List<Vector> getPacketVectors(List<Vector> path, Vector previous) {
		List<Vector> packets = new ArrayList<>();
		for (Vector v : path) {
			double distance = v.distanceTo(previous);
			if (distance > (infiniteVClipAllowed ? 10 : maxSpeed)) {
				int max = (infiniteVClipAllowed ? 10 : maxSpeed);
				Vector delta = v.copy().subtract(previous).normalize().multiply(max);
				for (int i = 0; i < distance / max; i++) {
					if (infiniteVClipAllowed) {
						packets.add(previous.add(delta.copy().multiply(i)));
					} else {
						packets.add(new Vector(previous.x, previous.y, previous.z));
					}
				}
			}

			packets.add(new Vector(v.x, v.y, v.z));
			previous = v;
		}
		return packets;
	}

	public List<PlayerMovePositionRotationPacket> getPackets(List<Vector> path, Vector previous, float pitch, float yaw) {
		List<PlayerMovePositionRotationPacket> packets = new ArrayList<>();
		for (Vector v : path) {
			double distance = v.distanceTo(previous);
			if (distance > 10) {
				for (int i = 0; i < distance / 10; i++) {
					packets.add(new PlayerMovePositionRotationPacket(previous.x, previous.y, previous.z, yaw, pitch));
				}
			}

			packets.add(new PlayerMovePositionRotationPacket(v.x, v.y, v.z, yaw, pitch));
			previous = v;
		}
		return packets;
	}

	public List<Vector> getBeamPath(Vector start, Vector end) {
		List<Vector> path = new ArrayList<>();
		Vector delta = end.copy().subtract(start);
		Vector step = delta.copy().normalize().multiply(maxSpeed);
		int steps = (int) (delta.length() / maxSpeed);
		for (int i = 0; i < steps; i++) {
			path.add(start.copy().add(step.copy().multiply(i)));
		}
		if (path.isEmpty() || !end.equals(path.get(path.size() - 1))) path.add(end);
		return path;
	}

}
