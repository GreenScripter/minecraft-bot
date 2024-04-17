package greenscripter.minecraft.utils;

public class RotationUtils {

	public static Vector getLookVec(float pitch, float yaw) {
		float f = 0.017453292F;
		float pi = (float) Math.PI;
		float f1 = (float) Math.cos(-yaw * f - pi);
		float f2 = (float) Math.sin(-yaw * f - pi);
		float f3 = (float) -Math.cos(-pitch * f);
		float f4 = (float) Math.sin(-pitch * f);
		return new Vector(f2 * f3, f4, f1 * f3);
	}

	public static Vector getMoveVec(float yaw) {
		float f = 0.017453292F;
		float pi = (float) Math.PI;
		float f1 = (float) Math.cos(-yaw * f - pi);
		float f2 = (float) Math.sin(-yaw * f - pi);
		float f3 = -(float) Math.cos(-0 * f);
		float f4 = (float) Math.sin(-0 * f);
		return new Vector(f2 * f3, f4, f1 * f3);
	}

	public static Rotation getNeededRotations(Vector eyePos, Vector vec) {
		double diffX = vec.x - eyePos.x;
		double diffY = vec.y - eyePos.y;
		double diffZ = vec.z - eyePos.z;
		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
		float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
		return new Rotation(yaw, pitch);
	}

	public static final class Rotation {

		private final float yaw;
		private final float pitch;

		public Rotation(float yaw, float pitch) {
			this.yaw = yaw;
			this.pitch = pitch;
		}

		public float getYaw() {
			return yaw;
		}

		public float getPitch() {
			return pitch;
		}
	}
}
