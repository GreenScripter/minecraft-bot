package com.greenscripter.minecraftbot.utils.data;

import java.io.IOException;

public interface IOBiConsumer<T, T2> {

	public void accept(T t, T2 t2) throws IOException;
}
