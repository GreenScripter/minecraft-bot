package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class BundleContentsComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:bundle_contents");

	public List<Slot> slots = new ArrayList<>();

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(slots.size());
		for (Slot e : slots) {
			out.writeSlot(e);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			slots.add(in.readSlot());
		}
	}

	public BundleContentsComponent copy() {
		BundleContentsComponent c = new BundleContentsComponent();
		for (Slot e : slots) {
			c.slots.add(e.copy());
		}
		return c;
	}

}
