package greenscripter.statemachine;

public class StateEvent<T> {

	public StateMachine<T> machine;
	public State<T> state;
	public T value;

	public StateEvent(StateMachine<T> machine, State<T> state, T value) {
		this.state = state;
		this.machine = machine;
		this.value = value;
	}

	public void swapToPrepare(String state) {
		machine.swapToPrepare(state);
	}

	public void swapTo(String state) throws ThrownReturn {
		machine.swapTo(state);
	}

	public void swapToNow(String state) throws ThrownReturn {
		machine.swapToNow(state);
	}

	public void pushPrepare(String state) {
		machine.pushPrepare(state);
	}

	public void push(String state) throws ThrownReturn {
		machine.push(state);
	}

	public void pushNow(String state) throws ThrownReturn {
		machine.pushNow(state);
	}

	public void swapToPrepare(State<T> state) {
		machine.swapToPrepare(state);
	}

	public void swapTo(State<T> state) throws ThrownReturn {
		machine.swapTo(state);
	}

	public void swapToNow(State<T> state) throws ThrownReturn {
		machine.swapToNow(state);
	}

	public void pushPrepare(State<T> state) {
		machine.pushPrepare(state);

	}

	public void push(State<T> state) throws ThrownReturn {
		machine.push(state);
	}

	public void pushNow(State<T> state) throws ThrownReturn {
		machine.pushNow(state);
	}

	public void popPrepare() {
		machine.popPrepare();
	}

	public void pop() throws ThrownReturn {
		machine.pop();
	}

	public void popNow() throws ThrownReturn {
		machine.popNow();
	}
}
