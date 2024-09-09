package greenscripter.minecraft.packet.s2c.play;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.packet.Packet;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class SystemChatPacket extends Packet {

	public static final int packetId = PacketIds.getS2CPlayId("minecraft:system_chat");

	public NBTComponent content;
	public boolean overlay;

	public SystemChatPacket() {}

	public SystemChatPacket(NBTComponent content) {
		this(content, false);
	}

	public SystemChatPacket(NBTComponent content, boolean overlay) {
		this.content = content;
		this.overlay = overlay;
	}

	public int id() {
		return packetId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeNBT(content);
		out.writeBoolean(overlay);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		content = in.readNBT();
		overlay = in.readBoolean();
	}

}
