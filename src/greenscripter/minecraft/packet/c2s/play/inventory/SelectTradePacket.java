package greenscripter.minecraft.packet.c2s.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SelectTradePacket extends Packet {

	public int selectedTrade;

	public SelectTradePacket() {}

	public SelectTradePacket(int selectedTrade) {
		this.selectedTrade = selectedTrade;
	}

	public int id() {
		return 0x2A;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(selectedTrade);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
