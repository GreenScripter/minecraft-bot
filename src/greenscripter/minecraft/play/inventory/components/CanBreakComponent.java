package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class CanBreakComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:can_break");

	public List<BlockPredicate> predicates = new ArrayList<>();
	public boolean showInTooltip = true;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(predicates.size());
		for (BlockPredicate e : predicates) {
			e.toBytes(out);
		}
		out.writeBoolean(showInTooltip);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			BlockPredicate e = new BlockPredicate();
			e.fromBytes(in);
		}
		showInTooltip = in.readBoolean();
	}

	public CanBreakComponent copy() {
		CanBreakComponent c = new CanBreakComponent();
		c.showInTooltip = showInTooltip;
		c.predicates.addAll(predicates.stream().map(BlockPredicate::copy).toList());
		return c;
	}

}
