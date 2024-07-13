package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PotionEffect {

	public int typeId;
	public Detail detail;

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(typeId);
		detail.toBytes(out);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		typeId = in.readVarInt();
		detail = new Detail();
		detail.fromBytes(in);
	}

	public PotionEffect copy() {
		PotionEffect e = new PotionEffect();
		e.typeId = typeId;
		e.detail = detail.copy();
		return e;
	}

	public static class Detail {

		public int amplifier;
		public int duration;
		public boolean ambient;
		public boolean showParticles;
		public boolean showIcon;
		public Detail hiddedEffect;

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeVarInt(amplifier);
			out.writeVarInt(duration);
			out.writeBoolean(ambient);
			out.writeBoolean(showParticles);
			out.writeBoolean(showIcon);
			out.writeBoolean(hiddedEffect != null);
			if (hiddedEffect != null) {
				hiddedEffect.toBytes(out);
			}
		}

		public void fromBytes(MCInputStream in) throws IOException {
			amplifier = in.readVarInt();
			duration = in.readVarInt();
			ambient = in.readBoolean();
			showParticles = in.readBoolean();
			showIcon = in.readBoolean();
			if (in.readBoolean()) {
				hiddedEffect = new Detail();
				hiddedEffect.fromBytes(in);
			}
		}

		public Detail copy() {
			Detail e = new Detail();
			e.amplifier = amplifier;
			e.duration = duration;
			e.ambient = ambient;
			e.showParticles = showParticles;
			e.showIcon = showIcon;
			if (hiddedEffect != null) {
				e.hiddedEffect = hiddedEffect.copy();
			}
			return e;
		}
	}
}