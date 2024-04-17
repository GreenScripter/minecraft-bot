package greenscripter.minecraft.play.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import greenscripter.minecraft.ServerConnection;

public class PlayData {

	public static Map<Class<? extends PlayData>, Supplier<? extends PlayData>> playData = new HashMap<>();

	static {
		PlayData.playData.put(PositionData.class, PositionData::new);
		PlayData.playData.put(RegistryData.class, RegistryData::new);
		PlayData.playData.put(WorldData.class, WorldData::new);
		PlayData.playData.put(PlayerData.class, PlayerData::new);
		PlayData.playData.put(ClientConfigData.class, ClientConfigData::new);
	}

	@SuppressWarnings("unchecked")
	public static <T extends PlayData> T createData(Class<T> type, ServerConnection sc) {
		T t = (T) playData.get(type).get();
		t.init(sc);
		return t;
	}

	public void init(ServerConnection sc) {

	}

}
