package greenscripter.minecraft.play.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.Registries;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.s2c.play.entity.EntityAttributesPacket;
import greenscripter.minecraft.packet.s2c.play.entity.EntityEffectPacket;
import greenscripter.minecraft.packet.s2c.play.entity.EntityEquipmentPacket;
import greenscripter.minecraft.packet.s2c.play.entity.EntityHeadRotationPacket;
import greenscripter.minecraft.packet.s2c.play.entity.EntityMetaDataPacket;
import greenscripter.minecraft.packet.s2c.play.entity.EntityPositionPacket;
import greenscripter.minecraft.packet.s2c.play.entity.EntityPositionRotationPacket;
import greenscripter.minecraft.packet.s2c.play.entity.EntityRotationPacket;
import greenscripter.minecraft.packet.s2c.play.entity.EntitySpawnPacket;
import greenscripter.minecraft.packet.s2c.play.entity.EntityVelocityPacket;
import greenscripter.minecraft.packet.s2c.play.entity.RemoveEntitiesPacket;
import greenscripter.minecraft.packet.s2c.play.entity.RemoveEntityEffectPacket;
import greenscripter.minecraft.packet.s2c.play.entity.SetPassengersPacket;
import greenscripter.minecraft.packet.s2c.play.entity.TeleportEntityPacket;
import greenscripter.minecraft.packet.s2c.play.entity.XPOrbSpawnPacket;
import greenscripter.minecraft.play.data.PlayerData;
import greenscripter.minecraft.play.data.WorldData;
import greenscripter.minecraft.world.World;
import greenscripter.minecraft.world.entity.Entity;

public class EntityPlayHandler extends PlayHandler {

	int attributesId = new EntityAttributesPacket().id();
	int addEffectId = new EntityEffectPacket().id();
	int equipmentId = new EntityEquipmentPacket().id();
	int headRotationId = new EntityHeadRotationPacket().id();
	int positionId = new EntityPositionPacket().id();
	int positionRotationId = new EntityPositionRotationPacket().id();
	int rotationId = new EntityRotationPacket().id();
	int spawnId = new EntitySpawnPacket().id();
	int velocityId = new EntityVelocityPacket().id();
	int removeId = new RemoveEntitiesPacket().id();
	int removeEffectId = new RemoveEntityEffectPacket().id();
	int setPassengersId = new SetPassengersPacket().id();
	int teleportId = new TeleportEntityPacket().id();
	int spawnXpOrbId = new XPOrbSpawnPacket().id();

	public void handlePacket(UnknownPacket up, ServerConnection sc) throws IOException {
		WorldData worldData = sc.getData(WorldData.class);
		World world = worldData.world;
		synchronized (world.worlds) {
			if (up.id == attributesId) {

			} else if (up.id == addEffectId) {
				EntityEffectPacket p = up.convert(new EntityEffectPacket());
				PlayerData player = sc.getData(PlayerData.class);

				//				System.out.println("From " + player.entityId + " add to " + p.entityId + " " + p.effectId + " " + p.duration + " " + p.amplifier);
			} else if (up.id == equipmentId) {
				EntityEquipmentPacket p = up.convert(new EntityEquipmentPacket());
				Entity e = world.getEntity(p.entityID);
				if (e != null) {
					for (int i = 0; i < p.slots.length; i++) {
						if (p.slots[i] != null) {
							//						System.out.println("Slot " + i + " set to " + p.slots[i]);
							e.slots[i] = p.slots[i];
						}
					}
				}

			} else if (up.id == headRotationId) {
				EntityHeadRotationPacket p = up.convert(new EntityHeadRotationPacket());

				Entity e = world.getEntity(p.entityID);
				if (e != null) {
					e.headYaw = p.headYaw * (360f / 256);
				}

			} else if (up.id == EntityMetaDataPacket.packetId) {
				EntityMetaDataPacket p = up.convert(new EntityMetaDataPacket());
				Entity e = world.getEntity(p.entityID);
				if (e != null) {
					if (p.meta == null) {
						System.err.println("Error setting entity " + e.type + " " + e.uuid + " metadata.");
					} else {
						for (int i = 0; i < p.meta.length; i++) {
							if (p.meta[i] != null) {
								e.metadata[i] = p.meta[i];
							}
						}
					}
				}
			} else if (up.id == positionId) {
				EntityPositionPacket p = up.convert(new EntityPositionPacket());

				Entity e = world.getEntity(p.entityID);
				if (e != null) {
					e.onGround = p.onGround;
					if (e.maintainer == sc) {
						e.pos.x += EntityPositionPacket.getFrom(p.deltaX);
						e.pos.y += EntityPositionPacket.getFrom(p.deltaY);
						e.pos.z += EntityPositionPacket.getFrom(p.deltaZ);
					}
				}

			} else if (up.id == positionRotationId) {
				EntityPositionRotationPacket p = up.convert(new EntityPositionRotationPacket());

				Entity e = world.getEntity(p.entityID);
				if (e != null) {
					e.onGround = p.onGround;
					if (e.maintainer == sc) {
						e.pos.x += EntityPositionPacket.getFrom(p.deltaX);
						e.pos.y += EntityPositionPacket.getFrom(p.deltaY);
						e.pos.z += EntityPositionPacket.getFrom(p.deltaZ);
					}
					e.pitch = p.pitch * (360f / 256);
					e.yaw = p.yaw * (360f / 256);
				}
			} else if (up.id == rotationId) {
				EntityRotationPacket p = up.convert(new EntityRotationPacket());

				Entity e = world.getEntity(p.entityID);
				if (e != null) {
					e.onGround = p.onGround;
					e.pitch = p.pitch * (360f / 256);
					e.yaw = p.yaw * (360f / 256);
				}
			} else if (up.id == spawnId) {
				EntitySpawnPacket p = up.convert(new EntitySpawnPacket());
				Entity e = world.getEntity(p.entityID);
				if (e != null) {
					e.headYaw = p.headYaw * (360f / 256);
					e.pitch = p.pitch * (360f / 256);
					e.yaw = p.yaw * (360f / 256);

					e.pos.x = p.x;
					e.pos.y = p.y;
					e.pos.z = p.z;
					e.maintainer = sc;
					e.players.add(sc);
				} else {
					e = new Entity();
					e.entityId = p.entityID;
					e.data = p.data;
					e.type = p.type;
					//				System.out.println("Spawned entity id " + e.entityId + " " + Registries.registriesFromIds.get("minecraft:entity_type").get(e.type));

					e.headYaw = p.headYaw * (360f / 256);
					e.pitch = p.pitch * (360f / 256);
					e.yaw = p.yaw * (360f / 256);

					e.pos.x = p.x;
					e.pos.y = p.y;
					e.pos.z = p.z;

					e.uuid = p.uuid;
					e.maintainer = sc;
					world.addEntityLoader(e, sc);

				}

			} else if (up.id == velocityId) {

			} else if (up.id == removeId) {
				RemoveEntitiesPacket p = up.convert(new RemoveEntitiesPacket());
				for (int i : p.ids) {
					world.unloadEntity(world.getEntity(i), sc);
				}
			} else if (up.id == removeEffectId) {

			} else if (up.id == setPassengersId) {

			} else if (up.id == teleportId) {
				TeleportEntityPacket p = up.convert(new TeleportEntityPacket());
				Entity e = world.getEntity(p.entityID);
				if (e != null) {
					e.onGround = p.onGround;
					e.pos.x = p.x;
					e.pos.y = p.y;
					e.pos.z = p.z;
					e.pitch = p.pitch * (360f / 256);
					e.yaw = p.yaw * (360f / 256);
				}
			} else if (up.id == spawnXpOrbId) {
				XPOrbSpawnPacket p = up.convert(new XPOrbSpawnPacket());
				Entity e = world.getEntity(p.entityID);
				if (e != null) {
					e.players.add(sc);
					e.pos.x = p.x;
					e.pos.y = p.y;
					e.pos.z = p.z;
				} else {
					e = new Entity();
					e.entityId = p.entityID;
					e.data = p.count;

					e.headYaw = 0;
					e.pitch = 0;
					e.yaw = 0;

					e.pos.x = p.x;
					e.pos.y = p.y;
					e.pos.z = p.z;

					e.uuid = UUID.randomUUID();
					e.type = Registries.registries.get("minecraft:entity_type").get("minecraft:experience_orb");
					world.addEntityLoader(e, sc);

				}
			}
		}

	}

	public void handleDisconnect(ServerConnection sc) {
		WorldData worldData = sc.getData(WorldData.class);
		if (worldData.world != null) {
			for (Entity e : new ArrayList<>(worldData.world.entities.values())) {
				worldData.world.unloadEntity(e, sc);
			}
		}
	}

	public List<Integer> handlesPackets() {
		return List.of(attributesId, addEffectId, equipmentId, headRotationId, EntityMetaDataPacket.packetId,  //
				positionId, positionRotationId, rotationId, spawnId, velocityId, removeId, //
				removeEffectId, setPassengersId, teleportId, spawnXpOrbId);
	}
}
