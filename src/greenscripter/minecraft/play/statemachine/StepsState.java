package greenscripter.minecraft.play.statemachine;

import java.util.ArrayList;
import java.util.List;

import greenscripter.minecraft.ServerConnection;
import greenscripter.statemachine.StateTickCallback;
import greenscripter.statemachine.StateTickPredicate;

public class StepsState extends PlayerState {

	public List<StateTickCallback<ServerConnection>> steps = new ArrayList<>();
	public int index = 0;
	public StateTickPredicate<ServerConnection> requirements = e -> false;
	public StateTickCallback<ServerConnection> prestep = null;
	public StateTickCallback<ServerConnection> interstage = null;

	public StepsState() {
		until(e -> !requirements.tick(e));
		onTick(e -> {
			if (index >= steps.size()) {
				e.pop();
			}
			//			System.out.println("Performing step " + index);
			if (prestep != null) {
				prestep.tick(e);
			}
			steps.get(index).tick(e);
			index++;
			if (interstage != null) {
				interstage.tick(e);
			}
		});
	}

	public StepsState next(StateTickCallback<ServerConnection> next) {
		steps.add(next);
		return this;
	}

}