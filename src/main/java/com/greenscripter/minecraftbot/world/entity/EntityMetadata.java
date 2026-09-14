package com.greenscripter.minecraftbot.world.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;
import com.greenscripter.minecraftbot.world.entity.metadata.EMArmadilloState;
import com.greenscripter.minecraftbot.world.entity.metadata.EMBlockState;
import com.greenscripter.minecraftbot.world.entity.metadata.EMBoolean;
import com.greenscripter.minecraftbot.world.entity.metadata.EMByte;
import com.greenscripter.minecraftbot.world.entity.metadata.EMCatVariant;
import com.greenscripter.minecraftbot.world.entity.metadata.EMDirection;
import com.greenscripter.minecraftbot.world.entity.metadata.EMFloat;
import com.greenscripter.minecraftbot.world.entity.metadata.EMFrogVariant;
import com.greenscripter.minecraftbot.world.entity.metadata.EMNBT;
import com.greenscripter.minecraftbot.world.entity.metadata.EMOBlockState;
import com.greenscripter.minecraftbot.world.entity.metadata.EMOGlobalPosition;
import com.greenscripter.minecraftbot.world.entity.metadata.EMOPosition;
import com.greenscripter.minecraftbot.world.entity.metadata.EMOTextComponent;
import com.greenscripter.minecraftbot.world.entity.metadata.EMOUUID;
import com.greenscripter.minecraftbot.world.entity.metadata.EMOVarInt;
import com.greenscripter.minecraftbot.world.entity.metadata.EMPaintingVariant;
import com.greenscripter.minecraftbot.world.entity.metadata.EMParticle;
import com.greenscripter.minecraftbot.world.entity.metadata.EMParticleList;
import com.greenscripter.minecraftbot.world.entity.metadata.EMPose;
import com.greenscripter.minecraftbot.world.entity.metadata.EMPosition;
import com.greenscripter.minecraftbot.world.entity.metadata.EMQuaternion;
import com.greenscripter.minecraftbot.world.entity.metadata.EMRotations;
import com.greenscripter.minecraftbot.world.entity.metadata.EMSlot;
import com.greenscripter.minecraftbot.world.entity.metadata.EMSnifferState;
import com.greenscripter.minecraftbot.world.entity.metadata.EMString;
import com.greenscripter.minecraftbot.world.entity.metadata.EMTextComponent;
import com.greenscripter.minecraftbot.world.entity.metadata.EMVarInt;
import com.greenscripter.minecraftbot.world.entity.metadata.EMVarLong;
import com.greenscripter.minecraftbot.world.entity.metadata.EMVector3;
import com.greenscripter.minecraftbot.world.entity.metadata.EMVillagerData;
import com.greenscripter.minecraftbot.world.entity.metadata.EMWolfVariant;

public abstract class EntityMetadata {

	public static Map<Integer, Supplier<EntityMetadata>> types = new HashMap<>();
	static {
		types.put(0, EMByte::new);
		types.put(1, EMVarInt::new);
		types.put(2, EMVarLong::new);
		types.put(3, EMFloat::new);
		types.put(4, EMString::new);
		types.put(5, EMTextComponent::new);
		types.put(6, EMOTextComponent::new);
		types.put(7, EMSlot::new);
		types.put(8, EMBoolean::new);
		types.put(9, EMRotations::new);
		types.put(10, EMPosition::new);
		types.put(11, EMOPosition::new);
		types.put(12, EMDirection::new);
		types.put(13, EMOUUID::new);
		types.put(14, EMBlockState::new);
		types.put(15, EMOBlockState::new);
		types.put(16, EMNBT::new);
		types.put(17, EMParticle::new);
		types.put(18, EMParticleList::new);
		types.put(19, EMVillagerData::new);
		types.put(20, EMOVarInt::new);
		types.put(21, EMPose::new);
		types.put(22, EMCatVariant::new);
		types.put(23, EMWolfVariant::new);
		types.put(24, EMFrogVariant::new);
		types.put(25, EMOGlobalPosition::new);
		types.put(26, EMPaintingVariant::new);
		types.put(27, EMSnifferState::new);
		types.put(28, EMArmadilloState::new);
		types.put(29, EMVector3::new);
		types.put(30, EMQuaternion::new);

	}

	public static EntityMetadata[] readMetadata(EntityMetadata[] existing, MCInputStream in) throws IOException {
		if (existing == null) existing = new EntityMetadata[40];
		int index = in.read();
		while (index != 0xFF) {
			int type = in.readVarInt();
			EntityMetadata em = types.get(type).get();
			em.read(in);
			existing[index] = em;
			index = in.read();
		}
		return existing;
	}

	public static void writeMetadata(EntityMetadata[] existing, MCOutputStream out) throws IOException {
		if (existing == null) existing = new EntityMetadata[40];
		int index = 0;
		for (EntityMetadata em : existing) {
			if (em != null) {
				out.write(index);
				out.writeVarInt(em.id());
				em.write(out);
			}
			index++;
		}

		out.write(0xFF);
	}

	public abstract int id();

	public abstract void read(MCInputStream in) throws IOException;

	public abstract void write(MCOutputStream out) throws IOException;

	public boolean isByte() {
		return this instanceof EMByte;
	}

	public boolean isVarInt() {
		return this instanceof EMVarInt;
	}

	public boolean isVarLong() {
		return this instanceof EMVarLong;
	}

	public boolean isFloat() {
		return this instanceof EMFloat;
	}

	public boolean isString() {
		return this instanceof EMString;
	}

	public boolean isTextComponent() {
		return this instanceof EMTextComponent;
	}

	public boolean isOTextComponent() {
		return this instanceof EMOTextComponent;
	}

	public boolean isSlot() {
		return this instanceof EMSlot;
	}

	public boolean isBoolean() {
		return this instanceof EMBoolean;
	}

	public boolean isRotations() {
		return this instanceof EMRotations;
	}

	public boolean isPosition() {
		return this instanceof EMPosition;
	}

	public boolean isOPosition() {
		return this instanceof EMOPosition;
	}

	public boolean isDirection() {
		return this instanceof EMDirection;
	}

	public boolean isOUUID() {
		return this instanceof EMOUUID;
	}

	public boolean isBlockState() {
		return this instanceof EMBlockState;
	}

	public boolean isOBlockState() {
		return this instanceof EMOBlockState;
	}

	public boolean isNBT() {
		return this instanceof EMNBT;
	}

	public boolean isParticle() {
		return this instanceof EMParticle;
	}

	public boolean isVillagerData() {
		return this instanceof EMVillagerData;
	}

	public boolean isOVarInt() {
		return this instanceof EMOVarInt;
	}

	public boolean isPose() {
		return this instanceof EMPose;
	}

	public boolean isCatVariant() {
		return this instanceof EMCatVariant;
	}

	public boolean isFrogVariant() {
		return this instanceof EMFrogVariant;
	}

	public boolean isOGlobalPosition() {
		return this instanceof EMOGlobalPosition;
	}

	public boolean isPaintingVariant() {
		return this instanceof EMPaintingVariant;
	}

	public boolean isSnifferState() {
		return this instanceof EMSnifferState;
	}

	public boolean isVector3() {
		return this instanceof EMVector3;
	}

	public boolean isQuaternion() {
		return this instanceof EMQuaternion;
	}

	public EMByte asByte() {
		return (EMByte) this;
	}

	public EMVarInt asVarInt() {
		return (EMVarInt) this;
	}

	public EMVarLong asVarLong() {
		return (EMVarLong) this;
	}

	public EMFloat asFloat() {
		return (EMFloat) this;
	}

	public EMString asString() {
		return (EMString) this;
	}

	public EMTextComponent asTextComponent() {
		return (EMTextComponent) this;
	}

	public EMOTextComponent asOTextComponent() {
		return (EMOTextComponent) this;
	}

	public EMSlot asSlot() {
		return (EMSlot) this;
	}

	public EMBoolean asBoolean() {
		return (EMBoolean) this;
	}

	public EMRotations asRotations() {
		return (EMRotations) this;
	}

	public EMPosition asPosition() {
		return (EMPosition) this;
	}

	public EMOPosition asOPosition() {
		return (EMOPosition) this;
	}

	public EMDirection asDirection() {
		return (EMDirection) this;
	}

	public EMOUUID asOUUID() {
		return (EMOUUID) this;
	}

	public EMBlockState asBlockState() {
		return (EMBlockState) this;
	}

	public EMOBlockState asOBlockState() {
		return (EMOBlockState) this;
	}

	public EMNBT asNBT() {
		return (EMNBT) this;
	}

	public EMParticle asParticle() {
		return (EMParticle) this;
	}

	public EMVillagerData asVillagerData() {
		return (EMVillagerData) this;
	}

	public EMOVarInt asOVarInt() {
		return (EMOVarInt) this;
	}

	public EMPose asPose() {
		return (EMPose) this;
	}

	public EMCatVariant asCatVariant() {
		return (EMCatVariant) this;
	}

	public EMFrogVariant asFrogVariant() {
		return (EMFrogVariant) this;
	}

	public EMOGlobalPosition asOGlobalPosition() {
		return (EMOGlobalPosition) this;
	}

	public EMPaintingVariant asPaintingVariant() {
		return (EMPaintingVariant) this;
	}

	public EMSnifferState asSnifferState() {
		return (EMSnifferState) this;
	}

	public EMVector3 asVector3() {
		return (EMVector3) this;
	}

	public EMQuaternion asQuaternion() {
		return (EMQuaternion) this;
	}
}
