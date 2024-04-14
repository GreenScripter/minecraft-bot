package greenscripter.statemachine;

public interface StateChangeCallback<T> {

	public void execute(StateChangeEvent<T> e) throws ThrownReturn;
}
