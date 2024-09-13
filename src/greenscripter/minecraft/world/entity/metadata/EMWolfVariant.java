package greenscripter.minecraft.world.entity.metadata;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMWolfVariant extends EntityMetadata {

	int variantIdPlusOne;
	String wildTexture;
	String tameTexture;
	String angryTexture;
	String biomeTag;
	List<Integer> biomes = new ArrayList<>();

	public int id() {
		return 23;
	}

	public void read(MCInputStream in) throws IOException {
		variantIdPlusOne = in.readVarInt();
		if (variantIdPlusOne == 0) {
			wildTexture = in.readString();
			tameTexture = in.readString();
			angryTexture = in.readString();
			int count = in.readVarInt();
			if (count == 0) {
				biomeTag = in.readString();
			} else {
				for (int i = 0; i < count - 1; i++) {
					biomes.add(in.readVarInt());
				}
			}
		}
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeVarInt(variantIdPlusOne);
		if (variantIdPlusOne == 0) {
			out.writeString(wildTexture);
			out.writeString(tameTexture);
			out.writeString(angryTexture);
			if (biomeTag != null) {
				out.writeVarInt(0);
				out.writeString(biomeTag);
			} else {
				out.writeVarInt(biomes.size() + 1);
				for (int i : biomes) {
					out.writeVarInt(i);
				}

			}
		}
	}

	public String toString() {
		return "EMWolfVariant [variantIdPlusOne=" + variantIdPlusOne + ", " + (wildTexture != null ? "wildTexture=" + wildTexture + ", " : "") + (tameTexture != null ? "tameTexture=" + tameTexture + ", " : "") + (angryTexture != null ? "angryTexture=" + angryTexture + ", " : "") + (biomeTag != null ? "biomeTag=" + biomeTag + ", " : "") + (biomes != null ? "biomes=" + biomes : "") + "]";
	}

}
