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
			Vector at = new Vector(pos.x, pos.y, pos.z);

			Vector delta = target.copy().subtract(at);
//			if (delta.y != 0) {
				delta.multiply(0, 1, 0);
//			}
			if (delta.length() > 10) {
				delta.normalize().multiply(10);
			}
			pos.x += delta.x;
			pos.y += delta.y;
			pos.z += delta.z;

			if (delta.length() == 0) {
				arrived = true;
				arrivedCount++;
			}
		} else {
			double coordinate = (System.currentTimeMillis() / 10000.0) % (2 * Math.PI);
			double slice = 2 * Math.PI / 1000;
			int botID = Integer.parseInt(sc.name.substring(3));
			coordinate += slice * botID;
			coordinate = coordinate % (2 * Math.PI);
			//			double offset = (System.currentTimeMillis() - start) / 1000.0;
			Vector target = new Vector(50 * Math.cos(coordinate), Math.abs((botID & 0x7) - 4) * (arrivedCount > 990 ? 1 : 0) + 200, 50 * Math.sin(coordinate));
			Vector at = new Vector(pos.x, pos.y, pos.z);

			Vector delta = target.copy().subtract(at);
			if (delta.y != 0) {
				delta.multiply(0, 1, 0);
			}
			if (delta.length() > 10) {
				delta.normalize().multiply(10);
			}
			pos.x += delta.x;
			pos.y += delta.y;
			pos.z += delta.z;
			//			System.out.println();
			//			System.out.println(Math.toDegrees(coordinate));
			//			System.out.println(Math.toDegrees(Math.atan2(delta.z, delta.x)));
			move.yaw = (float) Math.toDegrees(coordinate) - 0;//-90 + (float) Math.toDegrees(Math.atan2(delta.z, delta.x));
		}

		move.x = pos.x;
		move.y = pos.y;
		move.z = pos.z;
		move.pitch = pos.pitch;
		sc.out.writePacket(move);
	}

	public boolean handlesTick() {
		return true;
	}
}
