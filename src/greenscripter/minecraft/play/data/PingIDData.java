package greenscripter.minecraft.play.data;

import java.util.concurrent.atomic.AtomicLong;

public class PingIDData implements PlayData {

	private AtomicLong nextID = new AtomicLong(0);

	public long nextID() {
		return nextID.getAndIncrement();
	}
}
