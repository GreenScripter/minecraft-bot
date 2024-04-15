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

}
