package com.greenscripter.minecraftbot.world;

public class ChunkSection {

	short nonAir;
	short fluidCount;
	PalettedContainer blocks;
	PalettedContainer biomes;

	public String toString() {
		return "ChunkSection blocks: " + nonAir + " block palette " + blocks + " biome palette " + biomes;
	}
}
