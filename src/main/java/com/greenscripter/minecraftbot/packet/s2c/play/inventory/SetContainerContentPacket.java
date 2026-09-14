package com.greenscripter.minecraftbot.packet.s2c.play.inventory;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.PacketIds;
import com.greenscripter.minecraftbot.packet.Packet;
import com.greenscripter.minecraftbot.play.inventory.OpenedScreen;
import com.greenscripter.minecraftbot.play.inventory.Slot;
import com.greenscripter.minecraftbot.utils.data.MCInputStream;
import com.greenscripter.minecraftbot.utils.data.MCOutputStream;

public class SetContainerContentPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:container_set_content");

	public int windowId;
	public int stateId;
	public Slot[] slots;
	public Slot cursor;

	public SetContainerContentPacket() {}

	public SetContainerContentPacket(OpenedScreen screen) {
		windowId = screen.windowId;
		stateId = screen.stateId;
		cursor = screen.cursor;
		slots = screen.slots;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(windowId);
		out.writeVarInt(stateId);
		out.writeVarInt(slots.length);
		for (Slot s : slots) {
			out.writeSlot(s);
		}
		out.writeSlot(cursor);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		//		byte[] data = in.readAllBytes();
		//		in = new MCInputStream(new ByteArrayInputStream(data));
		windowId = in.readVarInt();
		stateId = in.readVarInt();
		int length = in.readVarInt();
		slots = new Slot[length];
		for (int i = 0; i < length; i++) {
			//			System.out.println("Reading slot " + i);
			slots[i] = in.readSlot();
			//			System.out.println("Read slot " + i + " as " + slots[i]);

		}
		cursor = in.readSlot();

		//		ByteArrayOutputStream out = new ByteArrayOutputStream();
		//		toBytes(new MCOutputStream(out));
		//		if (!Arrays.equals(data, out.toByteArray())) {
		//			System.out.println("Unequal serialization: ");
		//			System.out.println("Original " + data.length + " result = " + out.toByteArray().length);
		//			System.out.println(Arrays.toString(data));
		//			System.out.println(Arrays.toString(out.toByteArray()));
		//			for (Slot s : slots) {
		//				System.err.println(s.toStringShort());
		//			}
		//			System.err.println("Cursor: ");
		//			System.err.println(cursor.toStringShort());
		//
		//		}
	}

}
