package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class BucketEntityDataComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:bucket_entity_data");

	public NBTComponent data;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeNBT(data);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		data = in.readNBT();
	}

	public BucketEntityDataComponent copy() {
		BucketEntityDataComponent c = new BucketEntityDataComponent();
		if (data != null) {
			c.data = data.copy();
		}
		return c;
	}

}
