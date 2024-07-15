package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class InstrumentComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:instrument");

	public int typeIdPlusOne = 1;
	public SoundEvent soundEvent;
	public float duration;
	public float range;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(typeIdPlusOne);
		if (typeIdPlusOne == 0) {
			soundEvent.toBytes(out);
			out.writeFloat(duration);
			out.writeFloat(range);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		typeIdPlusOne = in.readVarInt();
		if (typeIdPlusOne == 0) {
			soundEvent = new SoundEvent();
			soundEvent.fromBytes(in);
			duration = in.readFloat();
			range = in.readFloat();
		}
	}

	public InstrumentComponent copy() {
		InstrumentComponent c = new InstrumentComponent();
		c.typeIdPlusOne = typeIdPlusOne;
		if (soundEvent != null) c.soundEvent = soundEvent.copy();
		c.duration = duration;
		c.range = range;
		return c;
	}

}
