package greenscripter.minecraft.utils;

public class Position {

	public int x;
	public int y;
	public int z;

	public Position() {}

	public Position(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Position)) {
			return false;
		}
		Position vec3i = (Position) o;
		if (this.x != vec3i.x) {
			return false;
		}
		if (this.y != vec3i.y) {
			return false;
		}
		return this.z == vec3i.z;
	}

	public int hashCode() {
		return (this.y + this.z * 31) * 31 + this.x;
	}

	public int getManhattanDistance(Position vec) {
		float dx = Math.abs(vec.x - this.x);
		float dy = Math.abs(vec.y - this.y);
		float dz = Math.abs(vec.z - this.z);
		return (int) (dx + dy + dz);
	}

	public String toString() {
		return x + " " + y + " " + z;
	}
}
