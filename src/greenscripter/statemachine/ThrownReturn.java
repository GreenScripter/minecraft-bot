package greenscripter.statemachine;

public class ThrownReturn extends Throwable {

	public ThrownReturn(String string) {
		super(string, null, true, false);
	}

}
