package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Position;

public class LodestoneTrackerComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:lodestone_tracker");

	public boolean hasGlobalPosition;
	public String dimension;
	public Position position;
	public boolean tracked;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeBoolean(hasGlobalPosition);
		if (hasGlobalPosition) {
			out.writeString(dimension);
			out.writePosition(position);
		}
		out.writeBoolean(tracked);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		hasGlobalPosition = in.readBoolean();
		if (hasGlobalPosition) {
			dimension = in.readString();
			position = in.readPosition();
		}
		tracked = in.readBoolean();
	}

	public LodestoneTrackerComponent copy() {
		LodestoneTrackerComponent c = new LodestoneTrackerComponent();
		c.hasGlobalPosition = hasGlobalPosition;
		c.dimension = dimension;
		c.tracked = tracked;
		if (position != null) c.position = position.copy();
		return c;
	}

}
