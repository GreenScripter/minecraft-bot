package greenscripter.minecraft.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import greenscripter.minecraft.gameinfo.BlockStates;
import greenscripter.minecraft.gameinfo.BlockStates.BlockState;
import greenscripter.minecraft.utils.Position;

public class TunnelPathFinder extends PathFinder {

	public boolean[] sideDanger = BlockStates.getBlockSet();
	{
		BlockStates.addToBlockSet(sideDanger, "minecraft:lava");
		BlockStates.addToBlockSet(sideDanger, "minecraft:water");
		BlockStates.addToBlockSet(sideDanger, "minecraft:gravel");
		for (BlockState s : BlockStates.idsToStates.values()) {
			if ("true".equals(s.properties().get("waterlogged"))) {
				sideDanger[s.id()] = true;
			}
		}
	}
	public boolean[] aboveDanger = BlockStates.getBlockSet();
	{
		BlockStates.addToBlockSet(aboveDanger, "minecraft:lava");
		BlockStates.addToBlockSet(aboveDanger, "minecraft:water");
		BlockStates.addToBlockSet(aboveDanger, "minecraft:sand");
		BlockStates.addToBlockSet(aboveDanger, "minecraft:gravel");
		BlockStates.addToBlockSet(aboveDanger, "minecraft:red_sand");
		for (BlockState s : BlockStates.idsToStates.values()) {
			if ("true".equals(s.properties().get("waterlogged"))) {
				aboveDanger[s.id()] = true;
			}
		}
	}
	public boolean[] fast = BlockStates.unionBlockSet(noCollides, noCollides);
	{
		BlockStates.addToBlockSet(fast, "minecraft:netherrack");
		BlockStates.addToBlockSet(fast, "minecraft:cave_air");
		BlockStates.addToBlockSet(fast, "minecraft:void_air");
	}

	public TunnelPathFinder() {
		maxSpeed = 1;
		restrictRadius = 10;
		noCollides = BlockStates.unionBlockSet(noCollides, noCollides);

		BlockStates.removeFromBlockSet(noCollides, "minecraft:water");

		BlockStates.addTagToBlockSet(noCollides, "minecraft:base_stone_overworld");
		BlockStates.addTagToBlockSet(noCollides, "minecraft:base_stone_nether");
		BlockStates.addTagToBlockSet(noCollides, "minecraft:terracotta");
		BlockStates.addToBlockSet(noCollides, "minecraft:sand");
		BlockStates.addToBlockSet(noCollides, "minecraft:red_sand");
		BlockStates.addToBlockSet(noCollides, "minecraft:gravel");
		BlockStates.addToBlockSet(noCollides, "minecraft:cobblestone");
		BlockStates.addToBlockSet(noCollides, "minecraft:dirt");
		BlockStates.addToBlockSet(noCollides, "minecraft:grass_block");
		BlockStates.addToBlockSet(noCollides, "minecraft:dripstone_block");
		BlockStates.addToBlockSet(noCollides, "minecraft:pointed_dripstone");

	}

	public boolean isPassible(Position p) {
		return isPassible(p.x, p.y, p.z);
	}

	public boolean isPassible(int x, int y, int z) {
		boolean initial = world.isPassiblePlayer(x, y, z, noCollides);
		if (initial) {
			if (world.isColliding(x, y + 2, z, aboveDanger)) {
				return false;
			}

			if (world.isColliding(x + 1, y, z, sideDanger)) {
				return false;
			}
			if (world.isColliding(x - 1, y, z, sideDanger)) {
				return false;
			}
			if (world.isColliding(x, y, z + 1, sideDanger)) {
				return false;
			}
			if (world.isColliding(x, y, z - 1, sideDanger)) {
				return false;
			}

			if (world.isColliding(x + 1, y + 1, z, sideDanger)) {
				return false;
			}
			if (world.isColliding(x - 1, y + 1, z, sideDanger)) {
				return false;
			}
			if (world.isColliding(x, y + 1, z + 1, sideDanger)) {
				return false;
			}
			if (world.isColliding(x, y + 1, z - 1, sideDanger)) {
				return false;
			}
		}
		return initial;
	}

	public boolean checkSides(int x, int y, int z) {
		if (world.isColliding(x + 1, y, z, sideDanger)) {
			return false;
		}
		if (world.isColliding(x - 1, y, z, sideDanger)) {
			return false;
		}
		if (world.isColliding(x, y, z + 1, sideDanger)) {
			return false;
		}
		if (world.isColliding(x, y, z - 1, sideDanger)) {
			return false;
		}
		return true;
	}

	public boolean isPassibleAir(int x, int y, int z) {
		return world.isPassiblePlayer(x, y, z, PathFinder.pathFindable);
	}

	List<AStarNode> add = new ArrayList<>();

	public void aStarNeighbors(AStarNode parent, List<AStarNode> blocks, Set<Long> endpoint) {
		super.aStarNeighbors(parent, add, endpoint);
		double basePriority = 1;

		for (int i = -10; i <= 10; i += 1) {
			if (isPassibleAir(parent.pos.x, parent.pos.y + i, parent.pos.z)) {
				basePriority = 1;
				basePriority -= Math.abs(i);
				AStarNode newNode = new AStarNode(new Position(parent.pos.x, parent.pos.y + i, parent.pos.z), parent, -basePriority);
				newNode.extraCost += Math.abs(i);
				if (Math.abs(i) == 10) {
					newNode.extraCost -= 10 + 1;
				}
				blocks.add(newNode);
			}
		}

		for (AStarNode n : add) {
			if (n.parent != null) {
				n.extraCost += 0.9;
			}
			int block = world.getBlock(n.pos);
			if (block > 0 && !fast[block]) {
				n.cost += 1;
			}
			block = world.getBlock(n.pos.x, n.pos.y + 1, n.pos.z);
			if (block > 0 && !fast[block]) {
				n.cost += 1;
			}
			blocks.add(n);
		}
		add.clear();
	}
}
