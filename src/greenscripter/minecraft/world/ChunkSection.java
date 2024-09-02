package greenscripter.minecraft.world;

public class ChunkSection {

	short nonAir;
	PalettedContainer blocks;
	PalettedContainer biomes;

	public String toString() {
		return "ChunkSection blocks: " + nonAir + " block palette " + blocks + " biome palette " + biomes;
	}
}