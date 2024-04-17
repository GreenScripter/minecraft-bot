package greenscripter.minecraft.world.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.utils.Vector;

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

}
