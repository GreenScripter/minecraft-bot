package greenscripter.minecraft.play.statemachine;

public class WaitState extends PlayerState {

	int timeout = 500;

	public WaitState(int time) {
		timeout = time;
		long start = System.currentTimeMillis();
		onTick(e -> {
			if (System.currentTimeMillis() - start > timeout) {
				e.pop();
			}
		});
	}
}
