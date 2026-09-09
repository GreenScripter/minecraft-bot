package com.greenscripter.minecraftbot.play.inventory.components;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class RepairCostComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:repair_cost");

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
