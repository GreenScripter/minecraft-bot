package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class OminousBottleAmplifierComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:ominous_bottle_amplifier");

	public int amplifier;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(amplifier);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		amplifier = in.readVarInt();
	}

	public OminousBottleAmplifierComponent copy() {
		OminousBottleAmplifierComponent c = new OminousBottleAmplifierComponent();
		c.amplifier = amplifier;
		return c;
	}

}
