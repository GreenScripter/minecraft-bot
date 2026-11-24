package com.greenscripter.minecraftbot.utils.data;

import java.io.IOException;

public interface IOSupplier<T> {

	public T get() throws IOException;
}
