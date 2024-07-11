package greenscripter.minecraft.packet.c2s.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SelectTradePacket extends Packet {

	public static final int packetId = PacketIds.getC2SPlayId("minecraft:select_trade");

	public int selectedTrade;

	public SelectTradePacket() {}

	public SelectTradePacket(int selectedTrade) {
		this.selectedTrade = selectedTrade;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(selectedTrade);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		throw new UnsupportedOperationException();
	}

}
