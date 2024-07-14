package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class CustomModelDataComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:custom_model_data");

	public int customModelData;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(customModelData);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		customModelData = in.readVarInt();
	}

	public CustomModelDataComponent copy() {
		CustomModelDataComponent c = new CustomModelDataComponent();
		c.customModelData = customModelData;
		return c;
	}

}
