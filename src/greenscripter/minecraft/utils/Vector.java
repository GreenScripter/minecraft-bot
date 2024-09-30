package greenscripter.minecraft.utils;

public class Vector {

	public double x;
	public double y;
	public double z;

	public Vector() {}

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(Position p) {
		this.x = p.x + 0.5;
		this.y = p.y;
		this.z = p.z + 0.5;
	}

	public Vector add(Vector v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}

	public Vector subtract(Vector v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		return this;
	}

	public Vector add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Vector multiply(double x, double y, double z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}

	public Vector multiply(double v) {
		this.x *= v;
		this.y *= v;
		this.z *= v;
		return this;
	}

	public Vector setX(double x) {
		this.x = x;
		return this;
	}

	public Vector setY(double y) {
		this.y = y;
		return this;
	}

	public Vector setZ(double z) {
		this.z = z;
		return this;
	}

	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public Vector normalize() {
		double length = this.length();
		x = x / length;
		y = y / length;
		z = z / length;
		return this;
	}

	public double distanceTo(Vector vec) {
		double d = vec.x - this.x;
		double e = vec.y - this.y;
		double f = vec.z - this.z;
		return Math.sqrt(d * d + e * e + f * f);
	}

	public double distanceTo(Position vec) {
		double d = vec.x - this.x;
		double e = vec.y - this.y;
		double f = vec.z - this.z;
		return Math.sqrt(d * d + e * e + f * f);
	}

	public double squaredDistanceTo(Vector vec) {
		double d = vec.x - this.x;
		double e = vec.y - this.y;
		double f = vec.z - this.z;
		return d * d + e * e + f * f;
	}

	public double squaredDistanceTo(Position vec) {
		double d = vec.x - this.x;
		double e = vec.y - this.y;
		double f = vec.z - this.z;
		return d * d + e * e + f * f;
	}

	public double squaredDistanceTo(double x, double y, double z) {
		double d = x - this.x;
		double e = y - this.y;
		double f = z - this.z;
		return d * d + e * e + f * f;
	}

	public Vector copy() {
		return new Vector(x, y, z);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Vector)) {
			return false;
		}
		Vector vec3d = (Vector) o;
		if (Double.compare(vec3d.x, this.x) != 0) {
			return false;
		}
		if (Double.compare(vec3d.y, this.y) != 0) {
			return false;
		}
		return Double.compare(vec3d.z, this.z) == 0;
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.x);
		int i = (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.y);
		i = 31 * i + (int) (l ^ l >>> 32);
		l = Double.doubleToLongBits(this.z);
		i = 31 * i + (int) (l ^ l >>> 32);
		return i;
	}

	public String toString() {
		return x + " " + y + " " + z;
	}

}
