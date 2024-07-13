package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class PotionContentsComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:potion_contents");

	public int potionId;
	public boolean hasCustomColor;
	public int customColor;

	public List<PotionEffect> effects = new ArrayList<>();

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(potionId);
		out.writeBoolean(hasCustomColor);
		if (hasCustomColor) out.writeInt(customColor);
		out.writeVarInt(effects.size());
		for (PotionEffect e : effects) {
			e.toBytes(out);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		potionId = in.readVarInt();
		hasCustomColor = in.readBoolean();
		if (hasCustomColor) customColor = in.readInt();
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			PotionEffect e = new PotionEffect();
			e.fromBytes(in);
			effects.add(e);
		}
	}

	public PotionContentsComponent copy() {
		PotionContentsComponent c = new PotionContentsComponent();
		c.potionId = potionId;
		c.hasCustomColor = hasCustomColor;
		c.customColor = customColor;
		for (PotionEffect e : effects) {
			c.effects.add(e.copy());
		}
		return c;
	}

}
