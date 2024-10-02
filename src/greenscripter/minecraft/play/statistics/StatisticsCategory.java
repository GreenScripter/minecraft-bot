package greenscripter.minecraft.play.statistics;

public enum StatisticsCategory {

	MINED(RegistryType.BLOCK), CRAFTED(RegistryType.ITEM), USED(RegistryType.ITEM), BROKEN(RegistryType.ITEM), PICKED_UP(RegistryType.ITEM), DROPPED(RegistryType.ITEM), KILLED(RegistryType.ENTITY), KILLED_BY(RegistryType.ENTITY), CUSTOM(null);

	public final RegistryType idType;

	StatisticsCategory(RegistryType idType) {
		this.idType = idType;
	}

	public enum RegistryType {
		BLOCK, ITEM, ENTITY
	}
}
