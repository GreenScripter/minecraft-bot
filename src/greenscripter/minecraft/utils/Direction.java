package greenscripter.minecraft.utils;

public enum Direction {

	DOWN(0, -1, 0), UP(0, 1, 0), NORTH(0, 0, -1), SOUTH(0, 0, 1), WEST(-1, 0, 0), EAST(1, 0, 0);

	public static final Direction NEGATIVE_X = WEST;
	public static final Direction NEGATIVE_Y = DOWN;
	public static final Direction NEGATIVE_Z = NORTH;

	public static final Direction POSITIVE_X = EAST;
	public static final Direction POSITIVE_Y = UP;
	public static final Direction POSITIVE_Z = SOUTH;

	final int x;
	final int y;
	final int z;

	private Direction(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Position getOffset() {
		return new Position(x, y, z);
	}

	public Direction flip() {
		switch (this) {
			case DOWN:
				return UP;
			case EAST:
				return WEST;
			case NORTH:
				return SOUTH;
			case SOUTH:
				return NORTH;
			case UP:
				return DOWN;
			case WEST:
				return EAST;
			default:
				return null;
		}
	}

	public Direction rotateClockwise() {
		switch (this) {
			case DOWN:
				return DOWN;
			case EAST:
				return SOUTH;
			case NORTH:
				return EAST;
			case SOUTH:
				return WEST;
			case UP:
				return UP;
			case WEST:
				return NORTH;
			default:
				return null;
		}
	}

	public Direction rotateCounterclockwise() {
		switch (this) {
			case DOWN:
				return DOWN;
			case EAST:
				return NORTH;
			case NORTH:
				return WEST;
			case SOUTH:
				return EAST;
			case UP:
				return UP;
			case WEST:
				return SOUTH;
			default:
				return null;
		}
	}

}
