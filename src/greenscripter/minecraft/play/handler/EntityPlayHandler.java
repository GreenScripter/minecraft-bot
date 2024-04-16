package greenscripter.minecraft.play.handler;

import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
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
import greenscripter.minecraft.play.data.WorldData;

public class EntityPlayHandler extends PlayHandler {

	int attributesId = new EntityAttributesPacket().id();
	int addEffectId = new EntityEffectPacket().id();
	int equipmentId = new EntityEquipmentPacket().id();
	int headRotationId = new EntityHeadRotationPacket().id();
	int metaDataId = new EntityMetaDataPacket().id();
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

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		WorldData worldData = sc.getData(WorldData.class);
		if (p.id == attributesId) {

		} else if (p.id == addEffectId) {

		} else if (p.id == equipmentId) {

		} else if (p.id == headRotationId) {

		} else if (p.id == metaDataId) {

		} else if (p.id == positionId) {

		} else if (p.id == positionRotationId) {

		} else if (p.id == rotationId) {

		} else if (p.id == spawnId) {

		} else if (p.id == velocityId) {

		} else if (p.id == removeId) {

		} else if (p.id == removeEffectId) {

		} else if (p.id == setPassengersId) {

		} else if (p.id == teleportId) {

		} else if (p.id == spawnXpOrbId) {

		}

	}

	public List<Integer> handlesPackets() {
		return List.of(attributesId, addEffectId, equipmentId, headRotationId, metaDataId, //
				positionId, positionRotationId, rotationId, spawnId, velocityId, removeId, //
				removeEffectId, setPassengersId, teleportId, spawnXpOrbId);
	}
}
