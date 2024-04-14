package greenscripter.statemachine;

import java.util.HashMap;
import java.util.Map;

public class Storage {

	Map<Class<?>, Map<String, Object>> storage = new HashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> type, String name) {
		var typeStorage = storage.get(type);
		if (typeStorage == null) return null;

		return (T) typeStorage.get(name);
	}

	public <T> void put(Class<T> type, String name, T t) {
		var typeStorage = storage.get(type);
		if (typeStorage == null) {
			typeStorage = new HashMap<String, Object>();
			storage.put(type, typeStorage);
		}

		typeStorage.put(name, t);
	}

}
