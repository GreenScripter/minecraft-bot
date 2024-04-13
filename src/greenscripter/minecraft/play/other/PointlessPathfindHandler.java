package greenscripter.minecraft.play.other;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionRotationPacket;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.play.handler.WorldPlayHandler;
import greenscripter.minecraft.play.state.PlayState;
import greenscripter.minecraft.play.state.PositionState;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.utils.Vector;
import greenscripter.minecraft.world.PathFinder;

public class PointlessPathfindHandler extends PlayHandler {

	WorldPlayHandler world;

	PathFinder finder;

	public PointlessPathfindHandler(WorldPlayHandler world) {
		this.world = world;
		finder = new PathFinder();
		finder.infiniteVClipAllowed = false;
		PlayState.playState.put(PathFindState.class, PathFindState::new);
	}

	static class PathFindState extends PlayState {

		List<PlayerMovePositionRotationPacket> queue = new ArrayList<>();
		Position oldPos = new Position(0, 72, 0);
		int tick = 0;
		boolean errored = false;

	}

	public void tick(ServerConnection sc) throws IOException {

		PositionState pos = sc.getState(PositionState.class);
		PathFindState pathState = sc.getState(PathFindState.class);
//		pathState.tick++;
//		if (pathState.tick % 2 != 0) return;
		if (pathState.errored) return;

		if (pos.dimension == null) {
			return;
		}
		if (finder.world == null || !finder.world.id.equals(pos.dimension)) {
			finder.world = world.worlds.getWorld(pos.dimension);
		}
		if (!pathState.queue.isEmpty()) {
			PlayerMovePositionRotationPacket p = pathState.queue.remove(0);
			//			System.out.println(pos.x + " " + pos.y + " " + pos.z + " move to " + p.x + " " + p.y + " " + p.z);
			pos.x = p.x;
			pos.y = p.y;
			pos.z = p.z;
			sc.out.writePacket(p);
			return;
		}

		//		if (System.currentTimeMillis() - start < 5000) {
		//			return;
		//		}
		long start = System.currentTimeMillis();
		List<Vector> path = finder.pathFind(new Vector(pos.x, pos.y, pos.z), pathState.oldPos);
		if (path == null) {
			if (System.currentTimeMillis() - start > 900) {
				System.out.println(pos.x + " " + pos.y + " " + pos.z + " -> " + pathState.oldPos.x + " " + pathState.oldPos.y + " " + pathState.oldPos.z);
				System.out.println(new Position((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z)));
				System.out.println(sc.name);
				pathState.errored = true;
			}
			return;
		}
		finder.mergeStraightLines(path, 10);
		pathState.queue.addAll(finder.getPackets(path, new Vector(pos.x, pos.y, pos.z), pos.pitch, pos.yaw));
		pathState.oldPos = new Position((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z));
		//		Collections.reverse(queue);
	}

	public boolean handlesTick() {
		return true;
	}
}
