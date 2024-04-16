package greenscripter.minecraft.world.entity;

import java.util.HashSet;
import java.util.Set;

import greenscripter.minecraft.ServerConnection;

public class Entity {

	public int entityId;
	public Set<ServerConnection> players = new HashSet<>();

}
