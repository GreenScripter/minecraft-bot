package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class StoredEnchantmentsComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:stored_enchantments");

	public List<Enchantment> enchantments = new ArrayList<>();
	public boolean showInTooltip = true;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(enchantments.size());
		for (Enchantment e : enchantments) {
			e.toBytes(out);
		}
		out.writeBoolean(showInTooltip);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			Enchantment e = new Enchantment();
			e.fromBytes(in);
			enchantments.add(e);
		}
		showInTooltip = in.readBoolean();
	}

	public StoredEnchantmentsComponent copy() {
		StoredEnchantmentsComponent c = new StoredEnchantmentsComponent();
		c.showInTooltip = showInTooltip;
		for (Enchantment e : enchantments) {
			c.enchantments.add(e.copy());
		}
		return c;
	}

}
