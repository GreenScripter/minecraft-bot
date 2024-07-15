package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class TrimComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:trim");

	public int materialTrimTypePlusOne = 1;

	public String assetName;
	public int ingredient;
	public float modelIndex;
	public List<Override> overrides = new ArrayList<>();
	public NBTComponent description;
	public int trimPatternType;
	public String assetName2;
	public int templateItem;
	public NBTComponent description2;
	public boolean decal;

	public boolean showInTooltip = true;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(materialTrimTypePlusOne);
		if (materialTrimTypePlusOne == 0) {
			out.writeString(assetName);
			out.writeVarInt(ingredient);
			out.writeFloat(modelIndex);
			out.writeVarInt(overrides.size());
			for (Override e : overrides) {
				e.toBytes(out);
			}
			out.writeNBT(description);
			out.writeVarInt(trimPatternType);
			out.writeString(assetName2);
			out.writeVarInt(templateItem);
			out.writeNBT(description2);
			out.writeBoolean(decal);
		}
		out.writeBoolean(showInTooltip);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		materialTrimTypePlusOne = in.readVarInt();
		if (materialTrimTypePlusOne == 0) {
			assetName = in.readString();
			ingredient = in.readVarInt();
			modelIndex = in.readFloat();
			int length = in.readVarInt();
			for (int i = 0; i < length; i++) {
				Override e = new Override();
				e.fromBytes(in);
				overrides.add(e);
			}
			description = in.readNBT();
			trimPatternType = in.readVarInt();
			assetName2 = in.readString();
			templateItem = in.readVarInt();
			description2 = in.readNBT();
			decal = in.readBoolean();
		}
		showInTooltip = in.readBoolean();
	}

	public TrimComponent copy() {
		TrimComponent c = new TrimComponent();
		c.materialTrimTypePlusOne = materialTrimTypePlusOne;
		c.assetName = assetName;
		c.ingredient = ingredient;
		c.modelIndex = modelIndex;
		if (description != null) c.description = description.copy();
		c.trimPatternType = trimPatternType;
		c.assetName2 = assetName2;
		c.templateItem = templateItem;
		if (description2 != null) c.description2 = description2.copy();
		c.decal = decal;
		c.showInTooltip = showInTooltip;

		for (Override e : overrides) {
			c.overrides.add(e.copy());
		}
		return c;
	}

	public static class Override {

		public NBTComponent content;
		public NBTComponent filteredContent;

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeNBT(content);
			out.writeBoolean(filteredContent != null);
			if (filteredContent != null) out.writeNBT(filteredContent);
		}

		public void fromBytes(MCInputStream in) throws IOException {
			content = in.readNBT();
			if (in.readBoolean()) filteredContent = in.readNBT();
		}

		public Override copy() {
			Override e = new Override();
			e.content = content.copy();
			if (filteredContent != null) e.filteredContent = filteredContent.copy();
			return e;
		}
	}

}
