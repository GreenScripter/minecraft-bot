package greenscripter.minecraft.play.handler;

import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.s2c.play.self.LoginPlayPacket;
import greenscripter.minecraft.packet.s2c.play.self.SetExperiencePacket;
import greenscripter.minecraft.packet.s2c.play.self.SetHealthPacket;
import greenscripter.minecraft.play.data.PlayerData;
import greenscripter.minecraft.utils.DimensionPosition;

public class PlayerPlayHandler extends PlayHandler {

	int setXPPacketId = new SetExperiencePacket().id();
	int setHealthId = new SetHealthPacket().id();
	int loginPlayId = new LoginPlayPacket().id();

	public void handlePacket(UnknownPacket up, ServerConnection sc) throws IOException {
		PlayerData player = sc.getData(PlayerData.class);
		if (up.id == setXPPacketId) {
			SetExperiencePacket p = up.convert(new SetExperiencePacket());
			player.experience.level = p.level;
			player.experience.totalXP = p.totalXP;
			player.experience.progress = p.progress;
		} else if (up.id == setHealthId) {
			SetHealthPacket p = up.convert(new SetHealthPacket());
			player.health = p.health;
			player.food = p.food;
			player.saturation = p.saturation;
		} else if (up.id == loginPlayId) {
			LoginPlayPacket p = up.convert(new LoginPlayPacket());
			player.entityId = p.entityId;
			if (p.hasDeathLocation) {
				player.deathLocation = new DimensionPosition(p.deathDimension, p.deathLocation.x, p.deathLocation.y, p.deathLocation.z);
			}
		}
	}

	public List<Integer> handlesPackets() {
		return List.of(setXPPacketId, setHealthId, loginPlayId);
	}
}
