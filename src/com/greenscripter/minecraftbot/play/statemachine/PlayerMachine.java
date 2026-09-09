package com.greenscripter.minecraftbot.play.statemachine;

import com.greenscripter.minecraftbot.ServerConnection;
import com.greenscripter.statemachine.StateMachine;

public class PlayerMachine extends StateMachine<ServerConnection> {

	public PlayerMachine(ServerConnection t) {
		super(t);
	}

}
