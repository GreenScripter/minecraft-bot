package greenscripter.minecraft.play.other;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.c2s.play.PlayerMovePositionRotationPacket;
import greenscripter.minecraft.play.data.PositionData;
import greenscripter.minecraft.play.handler.PlayHandler;
import greenscripter.minecraft.utils.Vector;

public class CirclePlayHandler extends PlayHandler {

	//	Set<String> arrived = new HashSet<>();
	boolean arrived = false;
	long start = System.currentTimeMillis();
	static int arrivedCount = 0;

	public void tick(ServerConnection sc) throws IOException {
		//		if (System.currentTimeMillis() - start < 5000) {
		//			return;
		//		}
		PositionData pos = sc.getData(PositionData.class);
		PlayerMovePositionRotationPacket move = new PlayerMovePositionRotationPacket();
		move.yaw = 0;

		if (!arrived) {
			Vector target = new Vector(0, 200, 0);
			Vector at = pos.pos.copy();

			Vector delta = target.copy().subtract(at);
			//			if (delta.y != 0) {
			delta.multiply(0, 1, 0);
			//			}
			if (delta.length() > 10) {
				delta.normalize().multiply(10);
			}
			pos.pos.x += delta.x;
			pos.pos.y += delta.y;
			pos.pos.z += delta.z;

			if (delta.length() == 0) {
				arrived = true;
				arrivedCount++;
			}
		} else {
			int botID = Integer.parseInt(sc.name.substring(3));

			if (botID < 100) {
				double coordinate = (System.currentTimeMillis() / 2000.0) % (2 * Math.PI);
				double overall = coordinate;
				double slice = 2 * Math.PI / 100;
				coordinate += slice * botID;
				coordinate = coordinate % (2 * Math.PI);
				Vector target = new Vector(10 * Math.cos(coordinate), 0, 10 * Math.sin(coordinate));
				double dx = target.x * Math.cos(overall) - target.y * Math.sin(overall);
				double dy = target.y * Math.cos(overall) + target.x * Math.sin(overall);
				target.x = dx;
				target.y = dy;
				target.y += 200;
				Vector at = pos.pos.copy();

				Vector delta = target.copy().subtract(at);
				if (delta.y >= 10) {
					delta.multiply(0, 1, 0);
				}
				if (delta.length() > 10) {
					delta.normalize().multiply(10);
				}
				pos.pos.x += delta.x;
				pos.pos.y += delta.y;
				pos.pos.z += delta.z;
				move.yaw = (float) Math.toDegrees(coordinate) - 0;
			} else if (botID < 200) {
				botID -= 100;

				double coordinate = (System.currentTimeMillis() / 2000.0) % (2 * Math.PI);
				double overall = coordinate;
				double slice = 2 * Math.PI / 100;
				coordinate += slice * botID;
				coordinate = coordinate % (2 * Math.PI);
				Vector target = new Vector(0, 200 - 10 * Math.cos(coordinate), 10 * Math.sin(coordinate));
				double dx = target.x * Math.cos(overall) - target.z * Math.sin(overall);
				double dz = target.z * Math.cos(overall) + target.x * Math.sin(overall);
				target.x = dx;
				target.z = dz;
				Vector at = pos.pos.copy();

				Vector delta = target.copy().subtract(at);
				if (delta.y >= 10) {
					delta.multiply(0, 1, 0);
				}
				if (delta.length() > 10) {
					delta.normalize().multiply(10);
				}
				pos.pos.x += delta.x;
				pos.pos.y += delta.y;
				pos.pos.z += delta.z;
				move.yaw = (float) Math.toDegrees(coordinate) - 0;
			} else if (botID < 300) {
				botID -= 200;
				double coordinate = (System.currentTimeMillis() / 2000.0) % (2 * Math.PI);
				double overall = -coordinate;
				double slice = 2 * Math.PI / 100;
				coordinate += slice * botID;
				coordinate = coordinate % (2 * Math.PI);
				Vector target = new Vector(10 * Math.cos(coordinate), 200 - 10 * Math.sin(coordinate), 0);
				double dx = target.x * Math.cos(overall) - target.z * Math.sin(overall);
				double dz = target.z * Math.cos(overall) + target.x * Math.sin(overall);
				target.x = dx;
				target.z = dz;
				Vector at = pos.pos.copy();

				Vector delta = target.copy().subtract(at);
				if (delta.y >= 10) {
					delta.multiply(0, 1, 0);
				}
				if (delta.length() > 10) {
					delta.normalize().multiply(10);
				}
				pos.pos.x += delta.x;
				pos.pos.y += delta.y;
				pos.pos.z += delta.z;
				move.yaw = (float) Math.toDegrees(coordinate) - 0;
			} else if (botID < 400) {
				botID -= 300;
				double coordinate = (System.currentTimeMillis() / 2000.0) % (2 * Math.PI);
				double overall = coordinate;
				double slice = 2 * Math.PI / 100;
				coordinate += slice * botID;
				coordinate = coordinate % (2 * Math.PI);
				Vector target = new Vector(10 * Math.cos(coordinate), 0, 10 * Math.sin(coordinate));
				double dz = target.z * Math.cos(overall) - target.y * Math.sin(overall);
				double dy = target.y * Math.cos(overall) + target.z * Math.sin(overall);
				target.z = dz;
				target.y = dy;
				target.y += 200;
				Vector at = pos.pos.copy();

				Vector delta = target.copy().subtract(at);
				if (delta.y >= 10) {
					delta.multiply(0, 1, 0);
				}
				if (delta.length() > 10) {
					delta.normalize().multiply(10);
				}
				pos.pos.x += delta.x;
				pos.pos.y += delta.y;
				pos.pos.z += delta.z;
				move.yaw = (float) Math.toDegrees(coordinate) - 0;
			}
		}

		move.x = pos.pos.x;
		move.y = pos.pos.y;
		move.z = pos.pos.z;
		move.pitch = 0;
		sc.sendPacket(move);
	}

	public boolean handlesTick() {
		return true;
	}
}
