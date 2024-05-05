package greenscripter.minecraft.play.statemachine;

public class WaitState extends PlayerState {

	int timeout = 500;
	long start;

	public WaitState(int time, long start) {
		timeout = time;
		onTick(e -> {
			if (System.currentTimeMillis() - start > timeout) {
				e.pop();
			}
		});
	}

	public WaitState(int time) {
		timeout = time;
		onInit(e -> {
			start = System.currentTimeMillis();
		});
		onTick(e -> {
			if (System.currentTimeMillis() - start > timeout) {
				e.pop();
			}
		});
	}

}
