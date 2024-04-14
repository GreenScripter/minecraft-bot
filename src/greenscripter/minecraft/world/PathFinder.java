package greenscripter.minecraft.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import greenscripter.minecraft.gameinfo.BlockStates;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionRotationPacket;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.utils.Vector;

public class PathFinder {

	public World world;

	public boolean[] noCollides = BlockStates.noCollideIds;

	public int pathRetainDistance = 10;

	public boolean infiniteVClipAllowed = true;

	private AtomicBoolean running = new AtomicBoolean(false);

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
		part = pathFind(start, new Vector(start.x, 322, start.z), radius);
		if (part == null) return null;
		path.addAll(part);
		path.addAll(getBeamPath(new Vector(start.x, 322, start.z), new Vector(end.x, 322, end.z)));
		part = pathFind(new Vector(end.x, 322, end.z), end, radius);
		if (part == null) return null;
		path.addAll(part);

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

	public List<Position> aStar(Position startBlock, List<Vector> endNear, Position endBlock, double nearLength) {

		if (startBlock == null || (endBlock == null && endNear.isEmpty())) {
			return null;
		}
		if (startBlock.equals(endBlock)) {
			List<Position> path = new ArrayList<>();
			path.add(startBlock);
			return path;
		}
		running.set(true);

		Set<Integer> endPoints = new HashSet<>();
		for (Vector pos : endNear) {
			endPoints.add(((int) Math.floor(pos.x) << 16) + (int) Math.floor(pos.z));
		}
		if (endBlock != null)
			endPoints.add((endBlock.x << 16) + endBlock.z);
		else
			endBlock = new Position((int) Math.floor(endNear.get(0).x), (int) Math.floor(endNear.get(0).y), (int) Math.floor(endNear.get(0).z));

		MaxHeap nodes = new MaxHeap(1000);

		Map<Position, AStarNode> seen = new HashMap<>();
		Set<Position> active = new HashSet<>();

		AStarNode startNode = new AStarNode(startBlock, null, 0);
		nodes.insert(startNode);

		seen.put(startNode.pos, startNode);
		active.add(startNode.pos);

		long start = System.currentTimeMillis();

		List<AStarNode> next = new ArrayList<>();
		loop: while (nodes.size() > 0) {
			if (System.currentTimeMillis() - start > 1000) {
				return null;
			}
			AStarNode current = nodes.extractMax();
			active.remove(current.pos);
			next.clear();
			aStarNeighbors(current, next, endPoints);

			for (AStarNode node : next) {
				node.cost += node.pos.getManhattanDistance(endBlock);

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

	public void aStarNeighbors(AStarNode parent, List<AStarNode> blocks, Set<Integer> endpoint) {
		Position current = parent.pos;
		double basePriority = 1;
		if (world.isPassiblePlayer(current.x + 1, current.y, current.z, noCollides)) blocks.add(new AStarNode(new Position(current.x + 1, current.y, current.z), parent, -basePriority));
		//		if ((basePriority = world.getState(current.x, current.y + 1, current.z)) >= 1) blocks.add(new AStarNode(new BlockPos(current.x, current.y + 1, current.z), parent, -basePriority));
		if (world.isPassiblePlayer(current.x, current.y, current.z + 1, noCollides)) blocks.add(new AStarNode(new Position(current.x, current.y, current.z + 1), parent, -basePriority));
		if (world.isPassiblePlayer(current.x - 1, current.y, current.z, noCollides)) blocks.add(new AStarNode(new Position(current.x - 1, current.y, current.z), parent, -basePriority));
		//		if ((basePriority = world.getState(current.x, current.y - 1, current.z)) >= 1) blocks.add(new AStarNode(new BlockPos(current.x, current.y - 1, current.z), parent, -basePriority));
		if (world.isPassiblePlayer(current.x, current.y, current.z - 1, noCollides)) blocks.add(new AStarNode(new Position(current.x, current.y, current.z - 1), parent, -basePriority));

		for (int i = -10; i <= 10; i += 1) {
			if (world.isPassiblePlayer(current.x, current.y + i, current.z, noCollides)) {
				basePriority = 1;
				basePriority -= Math.abs(i);
				AStarNode newNode = new AStarNode(new Position(current.x, current.y + i, current.z), parent, -basePriority);
				newNode.extraCost += Math.abs(i);
				blocks.add(newNode);
			}
		}
		if (infiniteVClipAllowed && endpoint.contains((current.x << 16) + current.z)) {
			for (int i = -66; i < 323; i += 1) {
				if (world.isPassiblePlayer(current.x, i, current.z, noCollides)) {
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

		return blockPathToCoordinates(aStar(startBlock, List.of(end), null, radius));
	}

	public List<Vector> aStar(Vector start, List<Vector> ends, double radius) {
		//		long startTime = System.nanoTime();
		Position startBlock = findValidStartNear(start);
		//		ChatUtils.message("Took " + (System.nanoTime() - startTime) + " nanoseconds to find valid start");

		//		startTime = System.nanoTime();
		List<Position> aStarResult = aStar(startBlock, ends, null, radius);
		//		ChatUtils.message("Took " + (System.nanoTime() - startTime) + " nanoseconds to a*");

		//		startTime = System.nanoTime();
		List<Vector> coordinates = blockPathToCoordinates(aStarResult);
		//		ChatUtils.message("Took " + (System.nanoTime() - startTime) + " nanoseconds to convert to coordinates");

		return coordinates;
	}

	public List<Vector> aStar(Vector start, Position end) {
		Position startBlock = findValidStartNear(start);
		//		System.out.println("Path start at " + startBlock+" original: "+start);
		return blockPathToCoordinates(aStar(startBlock, List.of(), end, -1));
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

	public Position findValidStartNear(Vector start) {
		Position first = new Position((int) Math.floor(start.x), (int) Math.floor(start.y), (int) Math.floor(start.z));
		//		MoveCheckPlayerEntity move = new MoveCheckPlayerEntity();
		double min = Double.POSITIVE_INFINITY;
		Position minPos = null;

		//		int block = world.getBlock(first.x, first.y, first.z);
		//		System.out.println(first+" "+start);
		//		System.out.println(block + " " + BlockStates.getState(block));
		if (world.isPassiblePlayer(first.x, first.y, first.z, noCollides)) {
			return first;
		}

		for (int distance = 0; distance <= 10; distance++) {
			for (int x = -distance; x <= distance; x++) {
				for (int y = -distance; y <= distance; y++) {
					for (int z = -distance; z <= distance; z++) {
						if (Math.abs(x) == distance || Math.abs(y) == distance || Math.abs(z) == distance) {
							if (start.squaredDistanceTo(first.x + x + 0.5, first.y + y, first.z + z + 0.5) <= min) {
								if (world.isPassiblePlayer(first.x + x, first.y + y, first.z + z, noCollides)) {
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
					if (world.isPassiblePlayer(first.x, y, first.z, noCollides)) {
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
		Vector step = delta.copy().normalize().multiply(10);
		int steps = (int) (delta.length() / 10);
		for (int i = 0; i < steps; i++) {
			path.add(start.copy().add(step.copy().multiply(i)));
		}
		path.add(end);
		return path;
	}

}
