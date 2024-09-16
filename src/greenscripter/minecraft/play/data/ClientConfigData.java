package greenscripter.minecraft.play.data;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.c2s.play.ClientInfoPacket;

public class ClientConfigData implements PlayData {

	public String locale = "en_US";
	public byte viewDistance = 0;
	public int chatMode = 0;
	public boolean chatColors = true;
	public byte skinParts = (byte) 0xFF;
	public int mainHand = 1;
	public boolean filtering = false;
	public boolean listing = false;
	ServerConnection sc;

	public void init(ServerConnection sc) {
		this.sc = sc;
	}

	public void setViewDistance(int b) {
		viewDistance = (byte) b;
		sc.sendPacket(createClientInfoPacket());
	}

	public ClientInfoPacket createClientInfoPacket() {
		ClientInfoPacket p = new ClientInfoPacket();
		p.locale = locale;
		p.viewDistance = viewDistance;
		p.chatMode = chatMode;
		p.chatColors = chatColors;
		p.skinParts = skinParts;
		p.mainHand = mainHand;
		p.filtering = filtering;
		p.listing = listing;
		return p;
	}
}
