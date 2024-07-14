package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class HideTooltipComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:hide_tooltip");

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {}

	public void fromBytes(MCInputStream in) throws IOException {}

	public HideTooltipComponent copy() {
		HideTooltipComponent c = new HideTooltipComponent();
		return c;
	}

}
