package greenscripter.minecraft.play.state;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PlayState {

	public static Map<Class<? extends PlayState>, Supplier<? extends PlayState>> playState = new HashMap<>();

	static {
		PlayState.playState.put(PositionState.class, PositionState::new);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends PlayState> T createState(Class<T> type) {
		return (T) playState.get(type).get();
	}

}
