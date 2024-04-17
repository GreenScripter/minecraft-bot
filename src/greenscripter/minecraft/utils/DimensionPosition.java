package greenscripter.minecraft.utils;

public class DimensionPosition extends Position {

	public String dimension;

	public DimensionPosition() {

	}

	public DimensionPosition(String dimension, int x, int y, int z) {
		super(x, y, z);
		this.dimension = dimension;
	}
}
