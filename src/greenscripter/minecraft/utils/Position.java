package greenscripter.minecraft.utils;

public class Position {

	public int x;
	public int y;
	public int z;

	public Position() {}

	public Position(long val) {
		x = (int) (val >> 38);
		y = (int) (val << 52 >> 52);
		z = (int) (val << 26 >> 38);
	}

	public Position(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Position(Vector v) {
		this(v.x, v.y, v.z);
	}

	public Position(double x, double y, double z) {
		this.x = (int) Math.floor(x);
		this.y = (int) Math.floor(y);
		this.z = (int) Math.floor(z);
	}

	public Position add(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Position add(Position other) {
		return add(other.x, other.y, other.z);
	}

	public Position multiply(int v) {
		this.x *= v;
		this.y *= v;
		this.z *= v;
		return this;
	}

	public Position multiply(int x, int y, int z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}

	public Position multiply(Position other) {
		return add(other.x, other.y, other.z);
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

	public long getEncoded() {
		return ((x & 0x3FFFFFFl) << 38) | ((z & 0x3FFFFFFl) << 12) | (y & 0xFFFl);
	}

	public Position copy() {
		return new Position(x, y, z);
	}

	public Vector center() {
		return new Vector(this);
	}

	public Vector corner() {
		return new Vector(x, y, z);
	}

	public String toString() {
		return x + " " + y + " " + z;
	}
}
