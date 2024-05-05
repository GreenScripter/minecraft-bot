package greenscripter.minecraft.play.statemachine;

public class WaitTicksState extends PlayerState {

	int timeout = 20;
	int ticks = 0;

	public WaitTicksState(int time) {
		timeout = time;
		onInit(e -> {
			ticks = 0;
		});
		onTick(e -> {
			ticks++;
			if (ticks >= timeout) {
				e.pop();
			}
		});
	}
}
