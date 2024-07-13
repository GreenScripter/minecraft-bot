package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class Enchantment {

	public int id;
	public int level;

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(id);
		out.writeVarInt(level);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		id = in.readVarInt();
		level = in.readVarInt();
	}

	public Enchantment copy() {
		Enchantment e = new Enchantment();
		e.id = id;
		e.level = level;
		return e;
	}
}