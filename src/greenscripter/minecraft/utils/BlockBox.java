package greenscripter.minecraft.utils;

import java.util.List;

public class BlockBox {

	public Position pos1;
	public Position pos2;

	public BlockBox() {
		pos1 = new Position();
		pos2 = new Position();
	}

	public BlockBox(Position a, Position b) {
		pos1 = new Position(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
		pos2 = new Position(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
	}

	public BlockBox(List<Position> pos) {
		if (pos.isEmpty()) return;
		pos1 = pos.get(0).copy();
		pos2 = pos.get(0).copy();
		for (Position p : pos) {
			pos1.x = Math.min(pos1.x, p.x);
			pos1.y = Math.min(pos1.y, p.y);
			pos1.z = Math.min(pos1.z, p.z);

			pos2.x = Math.max(pos2.x, p.x);
			pos2.y = Math.max(pos2.y, p.y);
			pos2.z = Math.max(pos2.z, p.z);

		}
	}

	public boolean contains(Position p) {
		if (p.x >= pos1.x && p.y >= pos1.y && p.z >= pos1.z && p.x <= pos2.x && p.y <= pos2.y && p.z <= pos2.z) {
			return true;
		}
		return false;
	}

	public boolean contains(Vector p) {
		if (p.x >= pos1.x && p.y >= pos1.y && p.z >= pos1.z && p.x <= pos2.x && p.y <= pos2.y && p.z <= pos2.z) {
			return true;
		}
		return false;
	}

	public BlockBox expand(int amount) {
		return new BlockBox(pos1.copy().add(-amount, -amount, -amount), pos2.copy().add(amount, amount, amount));
	}

	public BlockBox copy() {
		return new BlockBox(pos2, pos1);
	}

	public Position getIndex(int i) {
		int rx = pos2.x + 1 - pos1.x;
		int ry = pos2.y + 1 - pos1.y;

		int a = (rx * ry);

		int tz = i / a;

		int b = i - a * tz;

		int ty = b / rx;
		int tx = b % rx;

		return new Position(pos1.x + tx, pos1.y + ty, pos1.z + tz);
	}

	public Position getIndexXZY(int i) {
		int rx = pos2.x + 1 - pos1.x;
		int rz = pos2.z + 1 - pos1.z;

		int a = (rx * rz);

		int ty = i / a;

		int b = i - a * ty;

		int tz = b / rx;
		int tx = b % rx;

		return new Position(pos1.x + tx, pos1.y + ty, pos1.z + tz);
	}

	public int getSize() {
		return (pos2.x + 1 - pos1.x) * (pos2.y + 1 - pos1.y) * (pos2.z + 1 - pos1.z);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pos1 == null) ? 0 : pos1.hashCode());
		result = prime * result + ((pos2 == null) ? 0 : pos2.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		BlockBox other = (BlockBox) obj;
		if (pos1 == null) {
			if (other.pos1 != null) return false;
		} else if (!pos1.equals(other.pos1)) return false;
		if (pos2 == null) {
			if (other.pos2 != null) return false;
		} else if (!pos2.equals(other.pos2)) return false;
		return true;
	}

	public String toString() {
		return "BlockBox [" + (pos1 != null ? "pos1=" + pos1 + ", " : "") + (pos2 != null ? "pos2=" + pos2 : "") + "]";
	}

}
