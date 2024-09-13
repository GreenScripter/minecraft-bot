package greenscripter.minecraft.play.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.io.IOException;

import greenscripter.minecraft.AsyncSwarmController;
import greenscripter.minecraft.ServerConnection;
import greenscripter.minecraft.gameinfo.PacketIds;
import greenscripter.minecraft.packet.UnknownPacket;
import greenscripter.minecraft.packet.s2c.play.PlayerInfoRemovePacket;
import greenscripter.minecraft.packet.s2c.play.PlayerInfoUpdatePacket;
import greenscripter.minecraft.packet.s2c.play.self.LoginPlayPacket;
import greenscripter.minecraft.packet.s2c.play.self.RespawnPacket;
import greenscripter.minecraft.play.data.PlayData;

public class ViewerTrackPlayHandler extends PlayHandler {

	public ViewerTrackPlayHandler() {
		if (!PlayData.playData.containsKey(ViewerTrackPlayData.class)) {
			PlayData.playData.put(ViewerTrackPlayData.class, ViewerTrackPlayData::new);
		}
	}

	int commandsId = PacketIds.getS2CPlayId("minecraft:commands");

	public void handlePacket(UnknownPacket p, ServerConnection sc) throws IOException {
		ViewerTrackPlayData data = sc.getData(ViewerTrackPlayData.class);
		if (p.id == LoginPlayPacket.packetId) {
			LoginPlayPacket loginPacket = p.convert(new LoginPlayPacket());
			data.loginPacket = loginPacket;
		}
		if (p.id == RespawnPacket.packetId) {
			RespawnPacket respawnPacket = p.convert(new RespawnPacket());
			data.loginPacket.dimensionType = respawnPacket.dimensionType;
			data.loginPacket.dimensionName = respawnPacket.dimensionName;
			data.loginPacket.seedHash = respawnPacket.seedHash;
			data.loginPacket.gamemode = respawnPacket.gamemode;
			data.loginPacket.previousGamemode = respawnPacket.previousGamemode;
			data.loginPacket.isDebug = respawnPacket.isDebug;
			data.loginPacket.isFlat = respawnPacket.isFlat;
			data.loginPacket.hasDeathLocation = respawnPacket.hasDeathLocation;
			if (data.loginPacket.hasDeathLocation) {
				data.loginPacket.deathDimension = respawnPacket.deathDimension;
				data.loginPacket.deathLocation = respawnPacket.deathLocation;
			}
			data.loginPacket.portalCooldown = respawnPacket.portalCooldown;
		}
		if (p.id == PlayerInfoUpdatePacket.packetId) {
			PlayerInfoUpdatePacket update = p.convert(new PlayerInfoUpdatePacket());
			synchronized (data.playerList) {
				for (var e : update.toPerform.entrySet()) {
					PlayerInfoUpdatePacket.Action existing = data.playerList.get(e.getKey());
					if (existing == null) {
						data.playerList.put(e.getKey(), e.getValue());
						existing = e.getValue();
					}
					existing.updateTo(e.getValue());
				}
			}

		}
		if (p.id == PlayerInfoRemovePacket.packetId) {

			PlayerInfoRemovePacket remove = p.convert(new PlayerInfoRemovePacket());
			synchronized (data.playerList) {
				for (UUID uuid : remove.uuids) {
					data.playerList.remove(uuid);
				}
			}
		}
		if (p.id == commandsId) {
			data.commands = p;
		}
	}

	public List<Integer> handlesPackets() {
		return List.of(LoginPlayPacket.packetId, RespawnPacket.packetId, PlayerInfoUpdatePacket.packetId, PlayerInfoRemovePacket.packetId, commandsId);
	}

	static class ViewerTrackPlayData implements PlayData {

		LoginPlayPacket loginPacket;
		UnknownPacket commands;
		AsyncSwarmController controller;
		Map<UUID, PlayerInfoUpdatePacket.Action> playerList = new HashMap<>();

	}
}
