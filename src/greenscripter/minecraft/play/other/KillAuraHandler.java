package greenscripter.minecraft.play.other;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.Registries;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.c2s.play.InteractEntityPacket;
import greenscripter.minecraft.packet.c2s.play.SwingArmPacket;
import greenscripter.minecraft.packet.s2c.play.entity.DamageEventPacket;
import greenscripter.minecraft.play.data.PlayData;
import greenscripter.minecraft.play.data.PlayerData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.world.World;
import greenscripter.minecraft.world.entity.Entity;

public class KillAuraHandler extends PlayHandler {

	Set<Integer> swarm = new HashSet<>();
	Predicate<Entity> target = e -> true;
	public boolean swingHand = false;
	{
		if (!PlayData.playData.containsKey(KillAuraData.class)) {
			PlayData.playData.put(KillAuraData.class, KillAuraData::new);
		}
	}

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
				.filter(this.target)//
				.min((e1, e2) -> (int) (e2.pos.squaredDistanceTo(player.pos.pos) * 10 - e1.pos.squaredDistanceTo(player.pos.pos) * 10))//
				.orElse(null);

		if (target != null) {
			player.lastSwing = System.currentTimeMillis();

			sc.sendPacket(new InteractEntityPacket(target.entityId, InteractEntityPacket.TYPE_ATTACK));
			if (swingHand) sc.sendPacket(new SwingArmPacket(SwingArmPacket.MAIN_HAND));
		}
	}

	public void handlePacket(UnknownPacket packet, ServerConnection sc) throws IOException {
		DamageEventPacket p = packet.convert(new DamageEventPacket());
		PlayerData player = sc.getData(PlayerData.class);

		if (p.entityID == player.entityId) {
			if (p.damagerIDPlusOne == 0) return;
			sc.getData(KillAuraData.class).lastHitBy = p.damagerIDPlusOne - 1;
			//			var rot = RotationUtils.getNeededRotations(player.pos.getEyePos(), en.pos.copy().add(0, 1.62, 0));
			//			player.pos.pitch = rot.getPitch();
			//			player.pos.yaw = rot.getYaw();
			//			sc.sendPacket(new PlayerMovePositionRotationPacket(player));
		}
	}

	public boolean handlesTick() {
		return true;
	}

	public List<Integer> handlesPackets() {
		return List.of(new DamageEventPacket().id());
	}

	public static class KillAuraData implements PlayData {

		public int lastHitBy = -1;
	}

}
