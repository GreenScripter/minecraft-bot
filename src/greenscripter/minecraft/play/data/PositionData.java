package greenscripter.minecraft.play.data;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionRotationPacket;
import greenscripter.minecraft.packet.c2s.play.PlayerMoveRotationPacket;
import greenscripter.minecraft.utils.Vector;

public class PositionData implements PlayData {

	public Vector pos = new Vector();
	public float pitch;
	public float yaw;
	public boolean onGround;

	public String dimension;

	public Vector getEyePos() {
		return pos.copy().add(0, 1.62, 0);
	}

	public void sendMovePacket(ServerConnection sc, Packet p) {
		if (p instanceof PlayerMovePositionRotationPacket p2) {
			setPosRotation(sc, p2);
			return;
		} else if (p instanceof PlayerMovePositionPacket p2) {
			setPos(sc, p2);
			return;
		} else if (p instanceof PlayerMoveRotationPacket p2) {
			setRotation(sc, p2);
			return;
		} else if (p instanceof PlayerMovePacket p2) {
			setOnGround(sc, p2);
			return;
		}
		throw new IllegalArgumentException(p + " is not a movement packet");
	}

	public void setRotation(ServerConnection sc, float pitch, float yaw) {
		PlayerMoveRotationPacket p = new PlayerMoveRotationPacket();
		p.pitch = pitch;
		p.yaw = yaw;
		p.onGround = onGround;
		setRotation(sc, p);
	}

	public void setPosRotation(ServerConnection sc, Vector v, float pitch, float yaw) {
		PlayerMovePositionRotationPacket p = new PlayerMovePositionRotationPacket();
		p.x = v.x;
		p.y = v.y;
		p.z = v.z;
		p.pitch = pitch;
		p.yaw = yaw;
		p.onGround = onGround;
		setPosRotation(sc, p);
	}

	public void setPos(ServerConnection sc, Vector v) {
		PlayerMovePositionPacket p = new PlayerMovePositionPacket();
		p.x = v.x;
		p.y = v.y;
		p.z = v.z;
		p.onGround = onGround;
		setPos(sc, p);
	}

	public void setPos(ServerConnection sc, PlayerMovePositionPacket p) {
		sc.sendPacket(p);
		pos.x = p.x;
		pos.y = p.y;
		pos.z = p.z;
		onGround = p.onGround;
	}

	public void setPosRotation(ServerConnection sc, PlayerMovePositionRotationPacket p) {
		sc.sendPacket(p);
		pos.x = p.x;
		pos.y = p.y;
		pos.z = p.z;
		pitch = p.pitch;
		yaw = p.yaw;
		onGround = p.onGround;
	}

	public void setRotation(ServerConnection sc, PlayerMoveRotationPacket p) {
		sc.sendPacket(p);
		pitch = p.pitch;
		yaw = p.yaw;
		onGround = p.onGround;
	}

	public void setOnGround(ServerConnection sc, PlayerMovePacket p) {
		sc.sendPacket(p);
		onGround = p.onGround;
	}

	public String toString() {
		return "PositionData [" + (pos != null ? "pos=" + pos + ", " : "") + "pitch=" + pitch + ", yaw=" + yaw + ", onGround=" + onGround + ", " + (dimension != null ? "dimension=" + dimension : "") + "]";
	}

}
