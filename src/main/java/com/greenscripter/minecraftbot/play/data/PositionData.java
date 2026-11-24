package com.greenscripter.minecraftbot.play.data;

import com.greenscripter.minecraftbot.ServerConnection;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.packet.c2s.play.ClientEndTick;
import com.greenscripter.minecraftbot.packet.c2s.play.PlayerMovePacket;
import com.greenscripter.minecraftbot.packet.c2s.play.PlayerMovePositionPacket;
import com.greenscripter.minecraftbot.packet.c2s.play.PlayerMovePositionRotationPacket;
import com.greenscripter.minecraftbot.packet.c2s.play.PlayerMoveRotationPacket;
import com.greenscripter.minecraftbot.utils.play.Vector;

public class PositionData implements PlayData {

	public Vector pos = new Vector();
	public float pitch;
	public float yaw;
	public int flags;

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
		p.flags = flags;
		setRotation(sc, p);
	}

	public void setPosRotation(ServerConnection sc, Vector v, float pitch, float yaw) {
		PlayerMovePositionRotationPacket p = new PlayerMovePositionRotationPacket();
		p.x = v.x;
		p.y = v.y;
		p.z = v.z;
		p.pitch = pitch;
		p.yaw = yaw;
		p.flags = flags;
		setPosRotation(sc, p);
	}

	public void setPos(ServerConnection sc, Vector v) {
		PlayerMovePositionPacket p = new PlayerMovePositionPacket();
		p.x = v.x;
		p.y = v.y;
		p.z = v.z;
		p.flags = flags;
		setPos(sc, p);
	}

	public void setPos(ServerConnection sc, PlayerMovePositionPacket p) {
		sendMove(sc, p);
		pos.x = p.x;
		pos.y = p.y;
		pos.z = p.z;
		flags = p.flags;
	}

	public void setPosRotation(ServerConnection sc, PlayerMovePositionRotationPacket p) {
		sendMove(sc, p);
		pos.x = p.x;
		pos.y = p.y;
		pos.z = p.z;
		pitch = p.pitch;
		yaw = p.yaw;
		flags = p.flags;
	}

	public void setRotation(ServerConnection sc, PlayerMoveRotationPacket p) {
		sendMove(sc, p);
		pitch = p.pitch;
		yaw = p.yaw;
		flags = p.flags;
	}

	public void setOnGround(ServerConnection sc, PlayerMovePacket p) {
		sendMove(sc, p);
		flags = p.flags;
	}

	protected void sendMove(ServerConnection sc, Packet p) {
		sc.sendPacket(p);
		sc.sendPacket(new ClientEndTick());
	}

	public String toString() {
		return "PositionData [" + (pos != null ? "pos=" + pos + ", " : "") + "pitch=" + pitch + ", yaw=" + yaw + ", flags=" + flags + ", " + (dimension != null ? "dimension=" + dimension : "") + "]";
	}

}
