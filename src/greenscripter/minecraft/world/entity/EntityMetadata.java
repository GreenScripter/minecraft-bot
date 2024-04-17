package greenscripter.minecraft.world.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.world.entity.metadata.EMBlockState;
import greenscripter.minecraft.world.entity.metadata.EMBoolean;
import greenscripter.minecraft.world.entity.metadata.EMByte;
import greenscripter.minecraft.world.entity.metadata.EMCatVariant;
import greenscripter.minecraft.world.entity.metadata.EMDirection;
import greenscripter.minecraft.world.entity.metadata.EMFloat;
import greenscripter.minecraft.world.entity.metadata.EMFrogVariant;
import greenscripter.minecraft.world.entity.metadata.EMNBT;
import greenscripter.minecraft.world.entity.metadata.EMOBlockState;
import greenscripter.minecraft.world.entity.metadata.EMOGlobalPosition;
import greenscripter.minecraft.world.entity.metadata.EMOPosition;
import greenscripter.minecraft.world.entity.metadata.EMOTextComponent;
import greenscripter.minecraft.world.entity.metadata.EMOUUID;
import greenscripter.minecraft.world.entity.metadata.EMOVarInt;
import greenscripter.minecraft.world.entity.metadata.EMPaintingVariant;
import greenscripter.minecraft.world.entity.metadata.EMParticle;
import greenscripter.minecraft.world.entity.metadata.EMPose;
import greenscripter.minecraft.world.entity.metadata.EMPosition;
import greenscripter.minecraft.world.entity.metadata.EMQuaternion;
import greenscripter.minecraft.world.entity.metadata.EMRotations;
import greenscripter.minecraft.world.entity.metadata.EMSlot;
import greenscripter.minecraft.world.entity.metadata.EMSnifferState;
import greenscripter.minecraft.world.entity.metadata.EMString;
import greenscripter.minecraft.world.entity.metadata.EMTextComponent;
import greenscripter.minecraft.world.entity.metadata.EMVarInt;
import greenscripter.minecraft.world.entity.metadata.EMVarLong;
import greenscripter.minecraft.world.entity.metadata.EMVector3;
import greenscripter.minecraft.world.entity.metadata.EMVillagerData;

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
		types.put(18, EMVillagerData::new);
		types.put(19, EMOVarInt::new);
		types.put(20, EMPose::new);
		types.put(21, EMCatVariant::new);
		types.put(22, EMFrogVariant::new);
		types.put(23, EMOGlobalPosition::new);
		types.put(24, EMPaintingVariant::new);
		types.put(25, EMSnifferState::new);
		types.put(26, EMVector3::new);
		types.put(27, EMQuaternion::new);

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

	public abstract int id();

	public abstract void read(MCInputStream in) throws IOException;
}
