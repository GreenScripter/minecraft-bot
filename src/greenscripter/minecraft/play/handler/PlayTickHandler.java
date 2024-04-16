package greenscripter.minecraft.play.handler;

import java.util.function.Consumer;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;

public class PlayTickHandler extends PlayHandler {

	public Consumer<ServerConnection> ticker;

	public PlayTickHandler() {}

	public PlayTickHandler(Consumer<ServerConnection> ticker) {
		this.ticker = ticker;
	}

	public void tick(ServerConnection sc) throws IOException {
		ticker.accept(sc);
	}

	public boolean handlesTick() {
		return true;
	}

}
