package greenscripter.minecraft.gameinfo;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.net.URISyntaxException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class PacketIds {

	public static void main(String[] args) {
		System.out.println(packetIdsClientbound);
		System.out.println(packetIdsServerbound);
		System.out.println(packetsFromIdsClientbound);
		System.out.println(packetsFromIdsServerbound);
	}

	public static Map<String, Map<String, Integer>> packetIdsClientbound = new HashMap<>();
	public static Map<String, Map<String, Integer>> packetIdsServerbound = new HashMap<>();
	public static Map<String, Map<Integer, String>> packetsFromIdsClientbound = new HashMap<>();
	public static Map<String, Map<Integer, String>> packetsFromIdsServerbound = new HashMap<>();

	public static Map<String, Integer> getClientPhase(String name) {
		return packetIdsClientbound.get(name);
	}

	public static Map<String, Integer> getServerPhase(String name) {
		return packetIdsServerbound.get(name);
	}

	public static int getClientPacketId(String phase, String name) {
		Map<String, Integer> ids = packetIdsClientbound.get(phase);
		if (ids == null) return -1;
		return ids.get(name);
	}

	public static int getServerPacketId(String phase, String name) {
		Map<String, Integer> ids = packetIdsServerbound.get(phase);
		if (ids == null) return -1;
		return ids.get(name);
	}

	public static String getClientPacketName(String phase, int name) {
		Map<Integer, String> ids = packetsFromIdsClientbound.get(phase);
		if (ids == null) return null;
		return ids.get(name);
	}

	public static String getServerPacketName(String phase, int name) {
		Map<Integer, String> ids = packetsFromIdsServerbound.get(phase);
		if (ids == null) return null;
		return ids.get(name);
	}

	public static int getClientPlayId(String name) {
		return getClientPacketId("play", name);
	}

	public static int getServerPlayId(String name) {
		return getServerPacketId("play", name);
	}

	public static String getClientPlayName(int name) {
		return getClientPacketName("play", name);
	}

	public static String getServerPlayName(int name) {
		return getServerPacketName("play", name);
	}

	static {
		long start = System.currentTimeMillis();
		try {
			String registriesString = ResourceExtractor.getJSON("greenscripter/minecraft/resources/reports/packets.json");
			Map<String, JsonElement> registries = JsonParser.parseString(registriesString).getAsJsonObject().asMap();
			for (var e : registries.entrySet()) {
				packetIdsClientbound.put(e.getKey(), new HashMap<>());
				packetIdsServerbound.put(e.getKey(), new HashMap<>());
				packetsFromIdsClientbound.put(e.getKey(), new HashMap<>());
				packetsFromIdsServerbound.put(e.getKey(), new HashMap<>());

				if (e.getValue().getAsJsonObject().get("clientbound") != null) {
					for (var e2 : e.getValue().getAsJsonObject().get("clientbound").getAsJsonObject().asMap().entrySet()) {
						packetIdsClientbound.get(e.getKey()).put(e2.getKey(), e2.getValue().getAsJsonObject().get("protocol_id").getAsInt());
						packetsFromIdsClientbound.get(e.getKey()).put(e2.getValue().getAsJsonObject().get("protocol_id").getAsInt(), e2.getKey());
					}
				}
				
				if (e.getValue().getAsJsonObject().get("serverbound") != null) {
					for (var e2 : e.getValue().getAsJsonObject().get("serverbound").getAsJsonObject().asMap().entrySet()) {
						packetIdsServerbound.get(e.getKey()).put(e2.getKey(), e2.getValue().getAsJsonObject().get("protocol_id").getAsInt());
						packetsFromIdsServerbound.get(e.getKey()).put(e2.getValue().getAsJsonObject().get("protocol_id").getAsInt(), e2.getKey());
					}
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Took " + (System.currentTimeMillis() - start) + " ms to load packet ids.");

	}

}
