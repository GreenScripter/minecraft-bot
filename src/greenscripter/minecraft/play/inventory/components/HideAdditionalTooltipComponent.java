package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class HideAdditionalTooltipComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:hide_additional_tooltip");

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {}

	public void fromBytes(MCInputStream in) throws IOException {}

	public HideAdditionalTooltipComponent copy() {
		HideAdditionalTooltipComponent c = new HideAdditionalTooltipComponent();
		return c;
	}

}
