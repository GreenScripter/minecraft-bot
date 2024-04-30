package greenscripter.minecraft.utils;

import java.util.Iterator;
import java.util.function.Function;

public class IndexIterator<E> implements Iterator<E> {

	int index;
	int max;
	Function<Integer, E> values;

	public IndexIterator(int max, Function<Integer, E> values) {
		this.max = max;
		this.values = values;
	}

	public boolean hasNext() {
		return index < max;
	}

	public E next() {
		return values.apply(index++);
	}

}
