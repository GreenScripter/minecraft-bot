package greenscripter.minecraft.play.data;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.c2s.play.InteractEntityPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerActionPacket;
import greenscripter.minecraft.packet.c2s.play.UseItemOnPacket;
import greenscripter.minecraft.packet.c2s.play.UseItemPacket;
import greenscripter.minecraft.utils.Position;
import greenscripter.minecraft.world.World;

public class WorldData implements PlayData {

	public World world;

	public int breakSeq;

	public void useItemOn(ServerConnection sc, int hand, Position pos, int face) {
		UseItemOnPacket p = new UseItemOnPacket(hand, pos, face, breakSeq++);
		sc.sendPacket(p);
	}

	public void useItem(ServerConnection sc, int hand) {
		sc.sendPacket(new UseItemPacket(hand, breakSeq++));
	}

	public void attackEntity(ServerConnection sc, int entityId) {
		sc.sendPacket(new InteractEntityPacket(entityId, InteractEntityPacket.TYPE_ATTACK));
	}

	public void interactEntity(ServerConnection sc, int entityId) {
		sc.sendPacket(new InteractEntityPacket(entityId, InteractEntityPacket.TYPE_INTERACT));
	}

	public void startBreaking(ServerConnection sc, int x, int y, int z) {
		this.startBreaking(sc, new Position(x, y, z));
	}

	public void startBreaking(ServerConnection sc, Position pos) {
		sc.sendPacket(new PlayerActionPacket(PlayerActionPacket.START_MINING, pos, (byte) 1, breakSeq++));
	}

	public boolean finishBreaking(ServerConnection sc, int x, int y, int z) {
		return this.finishBreaking(sc, new Position(x, y, z));
	}

	public boolean finishBreaking(ServerConnection sc, Position pos) {
		if (world.getBlock(pos) == 0) {
			return true;
		}
		sc.sendPacket(new PlayerActionPacket(PlayerActionPacket.FINISH_MINING, pos, (byte) 1, breakSeq++));
		return false;
	}

}
