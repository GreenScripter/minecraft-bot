package greenscripter.statemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The StateMachine functions are:
 * "first" makes a new state and sets it as the starting state.
 * "state" or first with a name makes a new state and gives it a name.
 * Those return State objects.
 * State objects have their own builders:
 * "then" registers a terminate handler that pushes another state as active either making a new one,
 * pushing a passed state object, or pushing by name. Pushing by name returns the machine itself to
 * avoid double initializing the state.
 * "until" pops the state when a condition is met on tick
 * "run" runs a lambda every time the state is ticked
 * "when" runs a lambda when a condition is met on tick
 * "init" runs a lambda when the state is pushed onto the stack
 * "onPause" runs a lambda right before the machine transitions to a substate.
 * "onResume" runs a lambda right before the machine transitions back to this state via another
 * state being popped.
 * "onCleanup" runs a lambda when the state is removed from the stack for any reason. There are
 * state swaps in addition to pushes, this triggers on both.
 * "onFinished" runs a lambda when the state is popped off cleanly. Used by then to push a following
 * state.
 * "done" returns the state machine.
 * function actions are performed in the order they are called, so .until(a).run(b) acts like while
 * (a){b}, .run(b).until(a) acts like do {b} while (a) etc.
 */
public class StateMachine<T> {

	public List<State<T>> stack = new ArrayList<>();
	Map<String, State<T>> namedStates = new HashMap<>();
	public Storage storage = new Storage();
	public T value;

	private Map<Class<?>, Long> profilingTimers = new HashMap<>();

	private boolean needsInit = false;
	private State<T> initWith = null;
	public boolean profiling = false;

	public List<Consumer<State<T>>> stateChangeListeners = new ArrayList<>();

	public StateMachine(T t) {
		this.value = t;
	}

	public void printProfiler() {
		for (var e : profilingTimers.entrySet()) {
			System.out.println(e.getKey() + ": " + e.getValue());
		}
	}

	private void onStateChange(State<T> s) {
		for (Consumer<State<T>> c : stateChangeListeners) {
			try {
				c.accept(s);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	/**
	 * Replace the state of this machine.
	 * Does not call any handlers on old states.
	 */
	public void setState(State<T> state) {
		stack.clear();
		if (state != null) pushPrepare(state);
	}

	public void setState(String state) {
		setState(namedStates.get(state));
	}

	/**
	 * Get the current state or null;
	 */
	public State<T> getState() {
		if (stack.isEmpty()) return null;
		return stack.get(stack.size() - 1);
	}

	public boolean tick() {
		for (State<?> s : stack) {
			s.ticksThisTick = 0;
		}
		return tickInternal();
	}

	public boolean tickInternal() {
		long start = System.currentTimeMillis();
		State<T> state = getState();
		if (state != null) {
			while (needsInit) {
				needsInit = false;
				try {
					state.init(initWith);
				} catch (ThrownReturn e) {
				}
				if (profiling) {
					profilingTimers.put(state.getClass(), profilingTimers.getOrDefault(state.getClass(), 0l) + (System.currentTimeMillis() - start));
				}
				start = System.currentTimeMillis();
				state = getState();
			}

			if (state != null) try {
				if (state.maxTicksPerTick <= 0 || state.maxTicksPerTick > state.ticksThisTick++) {
					state.tick();
				}
			} catch (ThrownReturn e) {
			}
		}
		if (profiling) {
			profilingTimers.put(state.getClass(), profilingTimers.getOrDefault(state.getClass(), 0l) + (System.currentTimeMillis() - start));
		}
		return state == null;
	}

	public void swapToPrepare(String state) {
		swapToPrepare(namedStates.get(state));
	}

	public void swapTo(String state) throws ThrownReturn {
		swapTo(namedStates.get(state));
	}

	public void swapToNow(String state) throws ThrownReturn {
		swapToNow(namedStates.get(state));
	}

	public void pushPrepare(String state) {
		pushPrepare(namedStates.get(state));
	}

	public void push(String state) throws ThrownReturn {
		push(namedStates.get(state));
	}

	public void pushNow(String state) throws ThrownReturn {
		pushNow(namedStates.get(state));
	}

	public void swapToPrepare(State<T> state) {
		state.machine = this;

		State<T> start = getState();
		if (start != null) try {
			stack.remove(stack.size() - 1).cleanup(state);
		} catch (ThrownReturn e) {
		}
		needsInit = true;
		initWith = start;

		onStateChange(state);
		stack.add(state);
	}

	public void swapTo(State<T> state) throws ThrownReturn {
		swapToPrepare(state);
		throw new ThrownReturn("swapped");
	}

	public void swapToNow(State<T> state) throws ThrownReturn {
		swapToPrepare(state);
		if (!profiling) tickInternal();
		throw new ThrownReturn("swapped");
	}

	public void pushPrepare(State<T> state) {
		state.machine = this;
		State<T> previous = getState();
		if (previous != null) {
			try {
				previous.transitTo(state);
			} catch (ThrownReturn e) {
			}
		}
		onStateChange(state);
		stack.add(state);
		needsInit = true;
		initWith = previous;

	}

	public void push(State<T> state) throws ThrownReturn {
		pushPrepare(state);
		throw new ThrownReturn("pushed");
	}

	public void pushNow(State<T> state) throws ThrownReturn {
		pushPrepare(state);
		if (!profiling) tickInternal();
		throw new ThrownReturn("pushed");
	}

	public void popPrepare() {
		State<T> start = getState();
		try {
			stack.remove(stack.size() - 1).cleanup(getState());
		} catch (ThrownReturn e) {
		}
		try {
			start.finished(getState());
		} catch (ThrownReturn e) {
		}
		if (getState() != null) {
			try {
				getState().transitFrom(start);
			} catch (ThrownReturn e) {
			}
		}
	}

	public void pop() throws ThrownReturn {
		popPrepare();
		throw new ThrownReturn("popped");
	}

	public void popNow() throws ThrownReturn {
		popPrepare();
		if (!profiling) tickInternal();
		throw new ThrownReturn("popped");
	}

	public State<T> first() {
		if (!stack.isEmpty()) {
			throw new IllegalStateException("Already has first state " + stack.get(0));
		}
		var s = new State<T>(this);
		setState(s);
		return s;
	}

	public State<T> first(String name) {
		if (!stack.isEmpty()) {
			throw new IllegalStateException("Already has first state " + stack.get(0));
		}
		State<T> add = new State<T>(this);
		namedStates.put(name, add);
		add.name = name;
		setState(add);
		return add;
	}

	public State<T> first(String name, State<T> add) {
		if (!stack.isEmpty()) {
			throw new IllegalStateException("Already has first state " + stack.get(0));
		}
		namedStates.put(name, add);
		add.name = name;
		setState(add);
		return add;
	}

	public State<T> state() {
		return new State<T>(this);
	}

	public State<T> state(String name) {
		State<T> add = new State<T>(this);
		namedStates.put(name, add);
		add.name = name;
		return add;
	}

	public State<T> state(String name, State<T> add) {
		namedStates.put(name, add);
		add.name = name;
		return add;
	}

	public State<T> getState(String name) {
		return namedStates.get(name);
	}

}
