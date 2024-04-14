package greenscripter.statemachine;

public interface StateTickCallback<T> {

	public void tick(StateEvent<T> e) throws ThrownReturn;
}
