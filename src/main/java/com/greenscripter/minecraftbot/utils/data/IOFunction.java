package com.greenscripter.minecraftbot.utils.data;

import java.io.IOException;

public interface IOFunction<T, R> {

	public R apply(T t) throws IOException;
}
