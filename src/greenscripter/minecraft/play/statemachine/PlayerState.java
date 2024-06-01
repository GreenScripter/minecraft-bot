package greenscripter.minecraft.play.statemachine;

import java.util.ArrayList;
import java.util.List;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.statemachine.State;

public class PlayerState extends State<ServerConnection> {

	public List<PlayHandler> gameHandlers = new ArrayList<>();
	public List<PlayHandler> stickyGameHandlers = new ArrayList<>();

	public PlayerState() {
		onInit(e -> {
			for (PlayHandler p : stickyGameHandlers) {
				e.value.addPlayHandler(p);
			}
			for (PlayHandler p : gameHandlers) {
				e.value.addPlayHandler(p);
			}
		});
		onPause(e -> {
			for (PlayHandler p : gameHandlers) {
				e.value.removePlayHandler(p);
			}
		});
		onResume(e -> {
			for (PlayHandler p : gameHandlers) {
				e.value.addPlayHandler(p);
			}
		});
		onCleanup(e -> {
			for (PlayHandler p : gameHandlers) {
				e.value.removePlayHandler(p);
			}
			for (PlayHandler p : stickyGameHandlers) {
				e.value.removePlayHandler(p);
			}
		});
	}

	/**
	 * Add a game handler that is only active while this state is active.
	 */
	public void addGameHandler(PlayHandler handler) {
		gameHandlers.add(handler);
	}

	/**
	 * Add a game handler that isn't removed until this state finishes.
	 */
	public void addStickyGameHandler(PlayHandler handler) {
		stickyGameHandlers.add(handler);
	}

}
