package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class EnchantmentGlintOverrideComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:enchantment_glint_override");

	public boolean hasGlint;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeBoolean(hasGlint);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		hasGlint = in.readBoolean();
	}

	public EnchantmentGlintOverrideComponent copy() {
		EnchantmentGlintOverrideComponent c = new EnchantmentGlintOverrideComponent();
		c.hasGlint = hasGlint;
		return c;
	}

}
