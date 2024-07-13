package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class RepairCostComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:repair_cost");

	public int repairCost;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(repairCost);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		repairCost = in.readVarInt();
	}

	public RepairCostComponent copy() {
		RepairCostComponent c = new RepairCostComponent();
		c.repairCost = repairCost;
		return c;
	}

}
