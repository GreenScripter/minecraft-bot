package greenscripter.minecraft.world.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.utils.Vector;
import greenscripter.minecraft.world.entity.metadata.EMByte;
import greenscripter.minecraft.world.entity.metadata.EMOTextComponent;
import greenscripter.minecraft.world.entity.metadata.EMVillagerData;

public class Entity {

	public int entityId;
	public ServerConnection maintainer;
	public Set<ServerConnection> players = new HashSet<>();
	public UUID uuid;
	public int type;
	public Vector pos = new Vector();
	public float pitch;
	public float yaw;
	public float headYaw;
	public int data;
	public boolean onGround;
	public EntityMetadata[] metadata = new EntityMetadata[40];
	public Slot[] slots = new Slot[6];

	public Entity() {
		metadata[0] = new EMByte();
		metadata[0].asByte().value = 0;

		metadata[1] = new EMOTextComponent();
		metadata[1].asOTextComponent().value = null;
	}

	public boolean onFire() {
		return (metadata[0].asByte().value & 0x01) == 1;
	}

	public boolean isCrouching() {
		return (metadata[0].asByte().value & 0x02) == 1;
	}

	public boolean isSprinting() {
		return (metadata[0].asByte().value & 0x08) == 1;
	}

	public boolean isSwimming() {
		return (metadata[0].asByte().value & 0x10) == 1;
	}

	public boolean isInvisible() {
		return (metadata[0].asByte().value & 0x20) == 1;
	}

	public boolean hasGlowing() {
		return (metadata[0].asByte().value & 0x40) == 1;
	}

	public boolean isElytraFlying() {
		return (metadata[0].asByte().value & 0x80) == 1;
	}

	public NBTComponent getName() {
		return metadata[1].asOTextComponent().value;
	}

	public Slot getSlotIfItem() {
		if (metadata[8] != null && metadata[8].isSlot()) {
			return metadata[8].asSlot().value;
		}
		return null;
	}

	public float getEntityHealth() {
		if (metadata[9] != null && metadata[9].isFloat()) {
			return metadata[8].asFloat().value;
		}
		return 0;
	}

	public boolean getTamedIfTameable() {
		if (metadata[17] != null && metadata[17].isByte()) {
			return (metadata[17].asByte().value & 0x04) != 0;
		}
		return false;
	}

	public UUID getOwnerIfTamed() {
		if (metadata[18] != null && metadata[18].isOUUID()) {
			return metadata[18].asOUUID().value;
		}
		return null;
	}

	public EMVillagerData getVillagerDataIfVillager() {
		if (metadata[18] != null && metadata[18].isVillagerData()) {
			return metadata[18].asVillagerData();
		}
		return null;
	}

	public float getPlayerAbsorption() {
		if (metadata[15] != null && metadata[15].isFloat()) {
			return metadata[15].asFloat().value;
		}
		return 0;
	}

	public boolean isBabyAnimal() {
		if (metadata[16] != null && metadata[16].isBoolean()) {
			return metadata[16].asBoolean().value;
		}
		return false;
	}

	public int getHookedEntity() {
		if (metadata[8] != null && metadata[8].isVarInt()) {
			return metadata[8].asVarInt().value;
		}
		return 0;
	}

	public int getProjectileOwner() {
		return data;
	}

	public boolean isCatchable() {
		if (metadata[9] != null && metadata[9].isBoolean()) {
			return metadata[9].asBoolean().value;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T extends EntityMetadata> T getMetadataAtOrNull(Class<T> type, int index) {
		if (metadata[index] == null) return null;
		if (type.isAssignableFrom(metadata[index].getClass())) {
			return (T) metadata[index];
		}
		return null;
	}
}
