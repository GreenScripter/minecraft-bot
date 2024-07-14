package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.play.inventory.components.BlockPredicate.BlockSet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class ToolComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:tool");

	public List<Rule> rules = new ArrayList<>();
	public float defaultMiningSpeed = 1;
	public int damagePerBlock = 1;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(rules.size());
		for (Rule e : rules) {
			e.toBytes(out);
		}
		out.writeFloat(defaultMiningSpeed);
		out.writeVarInt(damagePerBlock);

	}

	public void fromBytes(MCInputStream in) throws IOException {
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			Rule e = new Rule();
			e.fromBytes(in);
			rules.add(e);
		}
		defaultMiningSpeed = in.readFloat();
		damagePerBlock = in.readVarInt();
	}

	public ToolComponent copy() {
		ToolComponent c = new ToolComponent();
		c.defaultMiningSpeed = defaultMiningSpeed;
		c.damagePerBlock = damagePerBlock;
		for (Rule e : rules) {
			c.rules.add(e.copy());
		}
		return c;
	}

	public static class Rule {

		public BlockSet blocks;
		public boolean hasSpeed;
		public float speed;
		public boolean hasCorrectDrop;
		public boolean correctDrop;

		public void toBytes(MCOutputStream out) throws IOException {
			blocks.toBytes(out);
			out.writeBoolean(hasSpeed);
			if (hasSpeed) out.writeFloat(speed);
			out.writeBoolean(hasCorrectDrop);
			if (hasCorrectDrop) out.writeBoolean(correctDrop);
		}

		public void fromBytes(MCInputStream in) throws IOException {
			blocks = new BlockSet();
			blocks.fromBytes(in);
			hasSpeed = in.readBoolean();
			if (hasSpeed) speed = in.readFloat();
			hasCorrectDrop = in.readBoolean();
			if (hasCorrectDrop) correctDrop = in.readBoolean();

		}

		public Rule copy() {
			Rule e = new Rule();
			e.blocks = blocks.copy();
			e.hasSpeed = hasSpeed;
			e.speed = speed;
			e.hasCorrectDrop = hasCorrectDrop;
			e.correctDrop = correctDrop;
			return e;
		}
	}

}
