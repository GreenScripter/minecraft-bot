package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class LoreComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:lore");

	public List<NBTComponent> lines = new ArrayList<>();

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(lines.size());
		for (NBTComponent c : lines) {
			out.writeNBT(c);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		lines.clear();
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			lines.add(in.readNBT());
		}
	}

	public LoreComponent copy() {
		LoreComponent c = new LoreComponent();
		for (NBTComponent nbt : lines) {
			c.lines.add(nbt.copy());
		}
		return c;
	}

}
