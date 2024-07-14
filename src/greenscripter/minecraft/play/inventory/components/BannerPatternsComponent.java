package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class BannerPatternsComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:banner_patterns");

	public List<Layer> layers = new ArrayList<>();

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(layers.size());
		for (Layer e : layers) {
			e.toBytes(out);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			Layer e = new Layer();
			e.fromBytes(in);
			layers.add(e);
		}
	}

	public BannerPatternsComponent copy() {
		BannerPatternsComponent c = new BannerPatternsComponent();
		for (Layer e : layers) {
			c.layers.add(e.copy());
		}
		return c;
	}

	public static class Layer {

		public int patternTypePlusOne;
		public String assetId;
		public String translationKey;
		public int dyeColor;

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeVarInt(patternTypePlusOne);
			if (patternTypePlusOne == 0) {
				out.writeString(assetId);
				out.writeString(translationKey);
			}
			out.writeVarInt(dyeColor);

		}

		public void fromBytes(MCInputStream in) throws IOException {
			patternTypePlusOne = in.readVarInt();
			if (patternTypePlusOne == 0) {
				assetId = in.readString();
				translationKey = in.readString();
			}
			dyeColor = in.readVarInt();

		}

		public Layer copy() {
			Layer c = new Layer();
			c.patternTypePlusOne = patternTypePlusOne;
			c.assetId = assetId;
			c.translationKey = translationKey;
			c.dyeColor = dyeColor;
			return c;
		}
	}

}
