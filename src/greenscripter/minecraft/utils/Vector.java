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

	public Vector copy() {
		return new Vector(x, y, z);
	}

}
