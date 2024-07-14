package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SuspiciousStewEffectsComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:suspicious_stew_effects");

	public List<Effect> effects = new ArrayList<>();

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(effects.size());
		for (Effect e : effects) {
			e.toBytes(out);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			Effect e = new Effect();
			e.fromBytes(in);
			effects.add(e);
		}
	}

	public SuspiciousStewEffectsComponent copy() {
		SuspiciousStewEffectsComponent c = new SuspiciousStewEffectsComponent();
		for (Effect e : effects) {
			c.effects.add(e.copy());
		}
		return c;
	}

	public static class Effect {

		public int potionId;
		public int duration;

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeVarInt(potionId);
			out.writeVarInt(duration);
		}

		public void fromBytes(MCInputStream in) throws IOException {
			potionId = in.readVarInt();
			duration = in.readVarInt();
		}

		public Effect copy() {
			Effect e = new Effect();
			e.potionId = potionId;
			e.duration = duration;
			return e;
		}
	}

}
