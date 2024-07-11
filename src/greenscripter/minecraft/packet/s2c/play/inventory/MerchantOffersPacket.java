package greenscripter.minecraft.packet.s2c.play.inventory;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.play.inventory.Slot;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class MerchantOffersPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:merchant_offers");

	public int windowId;
	public Trade[] trades;
	public int villagerLevel;
	public int totalXp;
	public boolean isRegularVillagerNotWanderingTrader;
	public boolean canRestock;

	public MerchantOffersPacket() {}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void fromBytes(MCInputStream in) throws IOException {
		windowId = in.readVarInt();
		trades = new Trade[in.readVarInt()];
		for (int i = 0; i < trades.length; i++) {
			Trade t = new Trade();

			t.item1 = in.readSlot();
			t.output = in.readSlot();
			t.item2 = in.readSlot();
			t.disabled = in.readBoolean();
			t.tradesUsedUp = in.readInt();
			t.maxTrades = in.readInt();
			t.xp = in.readInt();
			t.specialPrice = in.readInt();
			t.priceMultiplier = in.readFloat();
			t.demand = in.readInt();

			trades[i] = t;
		}
		villagerLevel = in.readVarInt();
		totalXp = in.readVarInt();
		isRegularVillagerNotWanderingTrader = in.readBoolean();
		canRestock = in.readBoolean();
	}

	public static class Trade {

		public Slot item1;
		public Slot output;
		public Slot item2;
		public boolean disabled;
		public int tradesUsedUp;
		public int maxTrades;
		public int xp;
		public int specialPrice;
		public float priceMultiplier;
		public int demand;

	}
}
