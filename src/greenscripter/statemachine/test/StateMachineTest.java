package greenscripter.statemachine.test;

import greenscripter.statemachine.State;
import greenscripter.statemachine.StateMachine;

public class StateMachineTest {

	public static void main(String[] args) throws InterruptedException {

		Data data = new Data();
		StateMachine<Data> machine = new StateMachine<>(data)//
				.first("count_up")//
				.until(s -> data.value >= 10)//
				.run(c -> {
					data.value++;
				})//
				.then(new WaitState<>(10))//
				.then()//
				.until(s -> data.value <= 0)//
				.run(c -> {
					data.value--;
				})//
				.then(new WaitState<>(10))//
				.then("count_up");

		while (true) {
			System.out.println("tick: " + data.value + " " + machine.getState());
			machine.tick();
			Thread.sleep(100);
		}
	}

	static class WaitState<T> extends State<T> {

		long duration = 0;
		long slept = 0;

		public WaitState(long duration) {
			this.duration = duration;
			init(c -> {
				slept = 0;
			});
			run(c -> {
				slept++;
				if (slept >= duration) {
					c.pop();
				}
			});
		}

	}

	static class Data {

		int value;
	}
}
