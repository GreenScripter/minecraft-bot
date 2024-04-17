package greenscripter.statemachine;

public interface StateTickPredicate<T> {

	public boolean tick(StateEvent<T> e) throws ThrownReturn;
}
