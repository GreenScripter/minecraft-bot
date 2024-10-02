package greenscripter.minecraft.play.data;

public class PingIDData implements PlayData {
	private long nextID = 0;

	public synchronized long nextID() {
		return nextID++;
	}
}
