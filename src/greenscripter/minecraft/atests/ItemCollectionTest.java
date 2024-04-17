package greenscripter.minecraft.atests;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import greenscripter.minecraft.AsyncSwarmController;
import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.Registries;
import greenscripter.minecraft.packet.c2s.play.ClientInfoPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionRotationPacket;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.handler.DeathPlayHandler;
import greenscripter.minecraft.play.handler.EntityPlayHandler;
import greenscripter.minecraft.play.handler.KeepAlivePlayHandler;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.play.handler.PlayTickHandler;
import greenscripter.minecraft.play.handler.PlayerPlayHandler;
import greenscripter.minecraft.play.handler.TeleportRequestPlayHandler;
import greenscripter.minecraft.play.handler.WorldPlayHandler;
import greenscripter.minecraft.play.statemachine.PlayerMachine;
import greenscripter.minecraft.utils.Vector;
import greenscripter.minecraft.world.PathFinder;
import greenscripter.minecraft.world.entity.Entity;
import greenscripter.statemachine.StateMachine;

public class ItemCollectionTest {

	public static void main(String[] args) throws Exception {
		WorldPlayHandler worldHandler = new WorldPlayHandler();
		List<PlayHandler> handlers = List.of(//
				new KeepAlivePlayHandler(), //
				new DeathPlayHandler(), //
				worldHandler, //
				new TeleportRequestPlayHandler(),//
				new EntityPlayHandler(),//
				new PlayerPlayHandler()//
		);

		Map<String, Set<Entity>> targets = new HashMap<>();
		Set<Integer> waitingOn = new HashSet<>();

		AsyncSwarmController controller = new AsyncSwarmController("localhost", 20255, handlers);
		controller.joinCallback = sc -> {
			sc.sendPacket(new ClientInfoPacket(10));
		};
		controller.localHandlers = sc -> {
			List<PlayHandler> local = new ArrayList<>();

			List<PlayerMovePositionRotationPacket> path = new ArrayList<>();
			PathFinder finder = new PathFinder();
			finder.timeout = 100;
			finder.infiniteVClipAllowed = false;
			AtomicInteger targetId = new AtomicInteger();
			StateMachine<ServerConnection> mp = new PlayerMachine(sc)//
					.first("wait")//
					.init(e -> targetId.set(-1))//
					.until(e -> finder.world != null && targets.get(finder.world.id) != null && !targets.get(finder.world.id).isEmpty())//
					.run(e -> {

						WorldData worldData = sc.getData(WorldData.class);
						if (worldData.world == null) {
							return;
						}

						PositionData pos = sc.getData(PositionData.class);
						if (pos.dimension == null) {
							return;
						}
						if (finder.world == null || !finder.world.id.equals(pos.dimension)) {
							finder.world = worldData.world;
						}
						//						System.out.println(targets.get(finder.world.id));
					}).then().run(event -> {
						PositionData pos = sc.getData(PositionData.class);
						Entity target = targets.get(pos.dimension).stream().min(Comparator.comparingDouble(e -> e.pos.squaredDistanceTo(pos.pos))).orElse(null);
						if (target == null) return;
						targets.get(pos.dimension).remove(target);
						waitingOn.add(target.entityId);
						targetId.set(target.entityId);
						event.popNow();
					}).then().run(event -> {
						PositionData pos = sc.getData(PositionData.class);
						Entity target = finder.world.getEntity(targetId.get());
						if (target == null) event.swapTo("wait");
						List<Vector> found = finder.pathFind(pos.pos.copy(), target.pos.copy(), 1);
						if (found != null) {
							path.addAll(finder.getPackets(found, pos.pos.copy(), pos.pitch, pos.yaw));
						} else {
							event.swapTo("wait");
						}

						event.swapTo("follow");
					}).name("pathfind").done();
			mp.state("follow")//
					.until(event -> path.isEmpty())//
					.run(event -> {
						PositionData pos = sc.getData(PositionData.class);
						PlayerMovePositionRotationPacket next = path.remove(0);
						pos.pos.x = next.x;
						pos.pos.y = next.y;
						pos.pos.z = next.z;
						event.value.sendPacket(next);
					})//
					.then()//
					.until(e -> {
						WorldData worldData = sc.getData(WorldData.class);
						Entity en = worldData.world.getEntity(targetId.get());
						if (en != null) {
							PositionData pos = sc.getData(PositionData.class);
							if (pos.pos.distanceTo(en.pos) > 1) {
								System.out.println("Re path find");
								e.push("pathfind");
							}
						}
						return en == null;
					})//
					.then("wait");

			local.add(new PlayTickHandler(sc2 -> {
				//				System.out.println(mp.getState());
				mp.tick();
			}));
			return local;
		};
		controller.tickCallback = () -> {
			for (var ent : worldHandler.worlds.worlds.entrySet()) {
				if (!targets.containsKey(ent.getKey())) {
					targets.put(ent.getKey(), new HashSet<>());
				}
				Set<Entity> worldTargets = targets.get(ent.getKey());
				ent.getValue().entities.values().stream()//
						.filter(e -> e.type == Registries.registries.get("minecraft:entity_type").get("minecraft:item"))//
						.filter(e -> !waitingOn.contains(e.entityId))//
						.forEach(worldTargets::add);
			}
		};
		controller.start();
		controller.connect(10, 40);

	}

}
