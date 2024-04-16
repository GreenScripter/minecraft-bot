package greenscripter.minecraft.world.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.utils.Vector;

public class Entity {

	public int entityId;
	public Set<ServerConnection> players = new HashSet<>();
	public UUID uuid;
	public int type;
	public Vector pos = new Vector();
	public float pitch;
	public float yaw;
	public float headYaw;
	public int data;
	public boolean onGround;
	
}
