package greenscripter.minecraft.play.other;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionRotationPacket;
import greenscripter.minecraft.play.data.PlayData;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.play.handler.WorldPlayHandler;
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
		PlayData.playData.put(PathFindData.class, PathFindData::new);
	}

	static class PathFindData implements PlayData {

		List<PlayerMovePositionRotationPacket> queue = new ArrayList<>();
		Position oldPos = new Position(0, 72, 0);
		int tick = 0;
		boolean errored = false;

	}

	public void tick(ServerConnection sc) throws IOException {

		PositionData pos = sc.getData(PositionData.class);
		PathFindData pathData = sc.getData(PathFindData.class);
		//		pathState.tick++;
		//		if (pathState.tick % 2 != 0) return;
		if (pathData.errored) return;

		if (pos.dimension == null) {
			return;
		}
		if (finder.world == null || !finder.world.id.equals(pos.dimension)) {
			finder.world = world.worlds.getWorld(pos.dimension);
		}
		if (!pathData.queue.isEmpty()) {
			PlayerMovePositionRotationPacket p = pathData.queue.remove(0);
			//			System.out.println(pos.x + " " + pos.y + " " + pos.z + " move to " + p.x + " " + p.y + " " + p.z);
			pos.pos.x = p.x;
			pos.pos.y = p.y;
			pos.pos.z = p.z;
			sc.out.writePacket(p);
			return;
		}

		//		if (System.currentTimeMillis() - start < 5000) {
		//			return;
		//		}
		long start = System.currentTimeMillis();
		List<Vector> path = finder.pathFind(pos.pos.copy(), pathData.oldPos);
		if (path == null) {
			if (System.currentTimeMillis() - start > 900) {
				System.out.println(pos.pos + " -> " + pathData.oldPos.x + " " + pathData.oldPos.y + " " + pathData.oldPos.z);
				System.out.println(new Position(pos.pos));
				System.out.println(sc.name);
				pathData.errored = true;
			}
			return;
		}
		finder.mergeStraightLines(path, 10);
		pathData.queue.addAll(finder.getPackets(path, pos.pos.copy(), pos.pitch, pos.yaw));
		pathData.oldPos = new Position(pos.pos);
		//		Collections.reverse(queue);
	}

	public boolean handlesTick() {
		return true;
	}
}
