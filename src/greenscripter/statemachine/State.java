package greenscripter.statemachine;

import java.util.ArrayList;
import java.util.List;

public class State<T> {

	public Storage storage = new Storage();
	protected StateMachine<T> machine;

	String name;

	List<StateTickCallback<T>> tickCallbacks = new ArrayList<>();

	List<StateChangeCallback<T>> initCallbacks = new ArrayList<>();
	List<StateChangeCallback<T>> transferToCallbacks = new ArrayList<>();
	List<StateChangeCallback<T>> transferFromCallbacks = new ArrayList<>();
	List<StateChangeCallback<T>> cleanupCallbacks = new ArrayList<>();
	List<StateChangeCallback<T>> finishedCallbacks = new ArrayList<>();
	
	public int maxTicksPerTick = 0;
	int ticksThisTick = 0;

	public State() {

	}

	public State(StateMachine<T> stateMachine) {
		machine = stateMachine;
	}

	protected void tick() throws ThrownReturn {
		for (StateTickCallback<T> c : tickCallbacks) {
			c.tick(new StateEvent<>(machine, this, machine.value));
		}
	}

	protected void init(State<T> previous) throws ThrownReturn {
		for (StateChangeCallback<T> c : initCallbacks) {
			c.execute(new StateChangeEvent<>(machine, this, machine.value, previous));
		}
	}

	protected void transitTo(State<T> target) throws ThrownReturn {
		for (StateChangeCallback<T> c : transferToCallbacks) {
			c.execute(new StateChangeEvent<>(machine, this, machine.value, target));
		}
	}

	protected void transitFrom(State<T> previous) throws ThrownReturn {
		for (StateChangeCallback<T> c : transferFromCallbacks) {
			c.execute(new StateChangeEvent<>(machine, this, machine.value, previous));
		}
	}

	protected void cleanup(State<T> previous) throws ThrownReturn {
		for (StateChangeCallback<T> c : cleanupCallbacks) {
			c.execute(new StateChangeEvent<>(machine, this, machine.value, previous));
		}
	}

	protected void finished(State<T> next) throws ThrownReturn {
		for (StateChangeCallback<T> c : finishedCallbacks) {
			c.execute(new StateChangeEvent<>(machine, this, machine.value, next));
		}
	}

	public State<T> init(StateChangeCallback<T> c) {
		return onInit(c);
	}

	public State<T> onInit(StateChangeCallback<T> c) {
		initCallbacks.add(c);
		return this;
	}

	public State<T> run(StateTickCallback<T> c) {
		return onTick(c);
	}

	public State<T> onRun(StateTickCallback<T> c) {
		return onTick(c);
	}

	public State<T> onTick(StateTickCallback<T> c) {
		tickCallbacks.add(c);
		return this;
	}

	public State<T> onPause(StateChangeCallback<T> c) {
		transferToCallbacks.add(c);
		return this;
	}

	public State<T> onResume(StateChangeCallback<T> c) {
		transferFromCallbacks.add(c);
		return this;
	}

	public State<T> onCleanup(StateChangeCallback<T> c) {
		cleanupCallbacks.add(c);
		return this;
	}

	public State<T> onFinished(StateChangeCallback<T> c) {
		finishedCallbacks.add(c);
		return this;
	}

	public State<T> until(StateTickPredicate<T> condition) {
		tickCallbacks.add(e -> {
			if (condition.tick(e)) {
				e.popNow();
			}
		});
		return this;
	}

	public State<T> when(StateTickPredicate<T> condition, StateTickCallback<T> action) {
		tickCallbacks.add(e -> {
			if (condition.tick(e)) {
				action.tick(e);
			}
		});
		return this;
	}

	public State<T> then() {
		State<T> next = machine.state();
		finishedCallbacks.add(e -> {
			e.machine.pushPrepare(next);
		});
		return next;
	}

	public State<T> name(String name) {
		return machine.state(name, this);
	}

	public State<T> then(State<T> next) {
		next.machine = machine;
		finishedCallbacks.add(e -> {
			e.machine.pushPrepare(next);
		});
		return next;
	}

	public StateMachine<T> then(String name) {
		finishedCallbacks.add(e -> {
			e.machine.pushPrepare(name);
		});
		return machine;
	}

	public StateMachine<T> done() {
		return machine;
	}

	public String toString() {
		return super.toString() + " " + name;
	}

}
