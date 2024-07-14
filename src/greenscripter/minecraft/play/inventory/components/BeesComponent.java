package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class BeesComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:bees");

	public List<Bee> bees = new ArrayList<>();

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(bees.size());
		for (Bee e : bees) {
			e.toBytes(out);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			Bee e = new Bee();
			e.fromBytes(in);
			bees.add(e);
		}
	}

	public BeesComponent copy() {
		BeesComponent c = new BeesComponent();
		for (Bee e : bees) {
			c.bees.add(e.copy());
		}
		return c;
	}

	public static class Bee {

		public NBTComponent data;
		public int ticksInHive;
		public int minTicksInHive;

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeNBT(data);
			out.writeVarInt(ticksInHive);
			out.writeVarInt(minTicksInHive);
		}

		public void fromBytes(MCInputStream in) throws IOException {
			data = in.readNBT();
			ticksInHive = in.readVarInt();
			minTicksInHive = in.readVarInt();
		}

		public Bee copy() {
			Bee c = new Bee();
			c.ticksInHive = ticksInHive;
			c.minTicksInHive = minTicksInHive;
			if (data != null) c.data = data.copy();
			return c;
		}
	}

}
