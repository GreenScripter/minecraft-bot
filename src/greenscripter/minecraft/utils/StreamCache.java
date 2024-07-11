package greenscripter.minecraft.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class StreamCache<T> {

	ConcurrentHashMap<Long, List<T>> storage = new ConcurrentHashMap<>();
	Supplier<T> more;

	public T get() {
		Long id = Thread.currentThread().getId();
		List<T> parts = storage.get(id);
		if (parts == null) {
			parts = new ArrayList<>();
			storage.put(id, parts);
		}
		if (parts.isEmpty()) {
			return more.get();
		} else {
			return parts.remove(parts.size() - 1);
		}
	}

	public void finish(T t) {
		Long id = Thread.currentThread().getId();
		List<T> parts = storage.get(id);
		if (parts == null) {
			parts = new ArrayList<>();
			storage.put(id, parts);
		}
		parts.add(t);
	}

	public static StreamCache<Pair<MCInputStream, ByteIn>> inCache;
	public static StreamCache<Pair<MCOutputStream, ByteOut>> outCache;

	static {
		inCache = new StreamCache<>();
		inCache.more = () -> {
			ByteIn bin = new ByteIn(new byte[0]);
			MCInputStream in = new MCInputStream(bin);
			return new Pair<>(in, bin);
		};

		outCache = new StreamCache<>();
		outCache.more = () -> {
			ByteOut bout = new ByteOut();
			MCOutputStream out = new MCOutputStream(bout);
			return new Pair<>(out, bout);
		};
	}

	public static Pair<MCInputStream, ByteIn> wrap(byte[] data) {
		Pair<MCInputStream, ByteIn> s = inCache.get();
		s.s.setBuffer(data);
		return s;
	}

	public static void doneIn(Pair<MCInputStream, ByteIn> in) {
		inCache.finish(in);
	}

	public static Pair<MCOutputStream, ByteOut> collect() {
		Pair<MCOutputStream, ByteOut> s = outCache.get();
		s.s.clear();
		return s;
	}

	public static void doneOut(Pair<MCOutputStream, ByteOut> out) {
		outCache.finish(out);
	}

	public static record Pair<T, S>(T t, S s) {}
}
