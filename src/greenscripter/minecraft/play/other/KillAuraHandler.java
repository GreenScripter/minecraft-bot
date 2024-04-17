package greenscripter.minecraft.play.other;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.Registries;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.InteractEntityPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionRotationPacket;
import greenscripter.minecraft.packet.c2s.play.SwingArmPacket;
import greenscripter.minecraft.packet.s2c.play.entity.DamageEventPacket;
import greenscripter.minecraft.play.data.PlayerData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.utils.RotationUtils;
import greenscripter.minecraft.world.World;
import greenscripter.minecraft.world.entity.Entity;

public class KillAuraHandler extends PlayHandler {

	Set<Integer> swarm = new HashSet<>();

	public void tick(ServerConnection sc) throws IOException {
		World world = sc.getData(WorldData.class).world;
		if (world == null) return;
		PlayerData player = sc.getData(PlayerData.class);
		if (System.currentTimeMillis() - player.lastSwing < 500) return;
		swarm.add(player.entityId);
		Entity target = world.entities.values().stream()//
				.filter(e -> Registries.safeAttack[e.type])//
				.filter(e -> !swarm.contains(e.entityId))//
				.filter(e -> e.pos.squaredDistanceTo(player.pos.pos) < 36)//
				.min((e1, e2) -> (int) (e2.pos.squaredDistanceTo(player.pos.pos) * 10 - e1.pos.squaredDistanceTo(player.pos.pos) * 10))//
				.orElse(null);

		if (target != null) {
			player.lastSwing = System.currentTimeMillis();
			//			if (sc.id == 0) System.out.println(sc.name + " " + Registries.registriesFromIds.get("minecraft:entity_type").get(target.type) + " " + target.pos.distanceTo(player.pos.pos));
			sc.sendPacket(new InteractEntityPacket(target.entityId, InteractEntityPacket.TYPE_ATTACK));
			sc.sendPacket(new SwingArmPacket(SwingArmPacket.MAIN_HAND));
			//			if (sc.id == 0) sc.sendPacket(new ExecuteCommandPacket("particle minecraft:block_marker minecraft:red_wool " + target.pos.copy().add(0, 1, 0)));
		}
	}

	public void handlePacket(UnknownPacket packet, ServerConnection sc) throws IOException {
		DamageEventPacket p = packet.convert(new DamageEventPacket());
		PlayerData player = sc.getData(PlayerData.class);

		if (p.entityID == player.entityId) {
			World world = sc.getData(WorldData.class).world;
			if (world == null) return;
			Entity en = world.getEntity(p.damagerIDPlusOne - 1);
			if (en == null) return;
			var rot = RotationUtils.getNeededRotations(player.pos.getEyePos(), en.pos.copy().add(0, 1.62, 0));
			player.pos.pitch = rot.getPitch();
			player.pos.yaw = rot.getYaw();
			sc.sendPacket(new PlayerMovePositionRotationPacket(player));
		}
	}

	public boolean handlesTick() {
		return true;
	}

	public List<Integer> handlesPackets() {
		return List.of(new DamageEventPacket().id());
	}

}
