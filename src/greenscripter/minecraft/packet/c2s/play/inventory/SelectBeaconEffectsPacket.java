package greenscripter.minecraft.packet.c2s.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SelectBeaconEffectsPacket extends Packet {

	public boolean hasPrimary;
	public int primaryId;
	public boolean hasSecondary;
	public int secondaryId;

	public SelectBeaconEffectsPacket() {

	}

	public SelectBeaconEffectsPacket(int primaryId) {
		this.hasPrimary = true;
		this.primaryId = primaryId;
	}

	public SelectBeaconEffectsPacket(int primaryId, int secondaryId) {
		this.hasPrimary = true;
		this.primaryId = primaryId;
		hasSecondary = true;
		this.secondaryId = secondaryId;
	}

	public int id() {
		return 0x2B;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeBoolean(hasPrimary);
		if (hasPrimary) {
			out.writeVarInt(primaryId);
		}
		out.writeBoolean(hasSecondary);
		if (hasSecondary) {
			out.writeVarInt(secondaryId);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
