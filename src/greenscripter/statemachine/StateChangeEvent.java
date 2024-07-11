package greenscripter.statemachine;

public class StateChangeEvent<T> extends StateEvent<T> {

	public State<T> other;

	public StateChangeEvent(StateMachine<T> machine, State<T> state, T value, State<T> other) {
		super(machine, state, value);
		this.other = other;
	}

}
