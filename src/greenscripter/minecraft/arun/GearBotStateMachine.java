package greenscripter.minecraft.arun;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.play.statemachine.PlayerMachine;

public class GearBotStateMachine extends PlayerMachine {

	public GearBotStateMachine(ServerConnection t) {
		super(t);
		setState(new FindBlocksState(GearBot.ironOre, true));
	}

}