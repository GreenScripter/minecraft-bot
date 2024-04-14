package greenscripter.statemachine;

public class StateMachineState<T> extends State<T> {

	public final StateMachine<T> wrappedMachine;
	public boolean resetOnRun = true;

	public StateMachineState(StateMachine<T> m) {
		this.wrappedMachine = m;
		if (m.stack.size() != 1) throw new IllegalArgumentException("State machine does not have a single starting state " + m.stack);
		State<T> first = m.stack.get(0);
		init(e -> {
			m.value = this.machine.value;
			if (resetOnRun) m.setState(first);
		});
		until(e -> m.tick());
	}
}
