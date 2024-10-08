package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class FoodComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:food");

	public int nutrition;
	public float saturation;
	public boolean canAlwaysEat;
	public float secondsToEat = 1.6f;
	public Slot convertsTo = new Slot();

	public List<Effect> effects = new ArrayList<>();

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(nutrition);
		out.writeFloat(saturation);
		out.writeBoolean(canAlwaysEat);
		out.writeFloat(secondsToEat);
		out.writeSlot(convertsTo);
		out.writeVarInt(effects.size());
		for (Effect e : effects) {
			e.toBytes(out);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		nutrition = in.readVarInt();
		saturation = in.readFloat();
		canAlwaysEat = in.readBoolean();
		secondsToEat = in.readFloat();
		convertsTo = in.readSlot();
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			Effect e = new Effect();
			e.fromBytes(in);
			effects.add(e);
		}
	}

	public FoodComponent copy() {
		FoodComponent c = new FoodComponent();
		c.nutrition = nutrition;
		c.saturation = saturation;
		c.canAlwaysEat = canAlwaysEat;
		c.secondsToEat = secondsToEat;
		if (convertsTo != null) c.convertsTo = convertsTo.copy();
		for (Effect e : effects) {
			c.effects.add(e.copy());
		}
		return c;
	}

	public static class Effect {

		public PotionEffect effect;
		public float probability = 1;

		public void toBytes(MCOutputStream out) throws IOException {
			effect.toBytes(out);
			out.writeFloat(probability);
		}

		public void fromBytes(MCInputStream in) throws IOException {
			effect = new PotionEffect();
			effect.fromBytes(in);
			probability = in.readFloat();
		}

		public Effect copy() {
			Effect e = new Effect();
			e.effect = effect.copy();
			e.probability = probability;
			return e;
		}
	}

}
