package com.greenscripter.minecraftbot.utils.data;

import java.io.IOException;

public interface IOConsumer<T> {

	public void accept(T t) throws IOException;
}
