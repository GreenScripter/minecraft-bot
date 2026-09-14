package com.greenscripter.minecraftbot.world.entity.metadata;

import java.io.IOException;

import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;
import com.greenscripter.minecraftbot.world.entity.EntityMetadata;

public class EMVillagerData extends EntityMetadata {

	public int villagerType;
	public int villagerProfession;
	public int villagerLevel;

	public int id() {
		return 19;
	}

	public void read(MCInputStream in) throws IOException {
		villagerType = in.readVarInt();
		villagerProfession = in.readVarInt();
		villagerLevel = in.readVarInt();
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeVarInt(villagerType);
		out.writeVarInt(villagerProfession);
		out.writeVarInt(villagerLevel);
	}
}
