package greenscripter.minecraft.play.statemachine;

import greenscripter.minecraft.ServerConnection;
import greenscripter.statemachine.StateMachine;

public class PlayerMachine extends StateMachine<ServerConnection> {

	public PlayerMachine(ServerConnection t) {
		super(t);
	}

}
