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

}
