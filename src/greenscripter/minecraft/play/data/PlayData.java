package greenscripter.minecraft.play.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import greenscripter.minecraft.ServerConnection;

public interface PlayData {

	public static Map<Class<? extends PlayData>, Supplier<? extends PlayData>> playData = new PlayDataMap();

	static class PlayDataMap extends HashMap<Class<? extends PlayData>, Supplier<? extends PlayData>> {

		PlayDataMap() {
			this.put(PositionData.class, PositionData::new);
			this.put(RegistryData.class, RegistryData::new);
			this.put(WorldData.class, WorldData::new);
			this.put(PlayerData.class, PlayerData::new);
			this.put(ClientConfigData.class, ClientConfigData::new);
			this.put(InventoryData.class, InventoryData::new);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends PlayData> T createData(Class<T> type, ServerConnection sc) {
		T t = (T) playData.get(type).get();
		t.init(sc);
		return t;
	}

	public default void init(ServerConnection sc) {

	}

}
