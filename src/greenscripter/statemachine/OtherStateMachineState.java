package greenscripter.statemachine;

public class OtherStateMachineState<T, V> extends State<T> {

	public final StateMachine<V> wrappedMachine;
	public boolean resetOnRun = true;

	public OtherStateMachineState(StateMachine<V> m) {
		this.wrappedMachine = m;
		if (m.stack.size() != 1) throw new IllegalArgumentException("State machine does not have a single starting state " + m.stack);
		State<V> first = m.stack.get(0);
		init(e -> {
			if (resetOnRun) m.setState(first);
		});
		until(e -> m.tick());
	}
}
