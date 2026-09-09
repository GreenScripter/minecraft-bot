package com.greenscripter.minecraftbot.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import com.greenscripter.minecraftbot.gameinfo.ComponentData;
import com.greenscripter.minecraftbot.play.inventory.Component;
import com.greenscripter.minecraftbot.utils.MCInputStream;
import com.greenscripter.minecraftbot.utils.MCOutputStream;

public class FireworksComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:fireworks");

	public int flightDuration;
	public List<FireworkExplosion> explosions = new ArrayList<>();

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(flightDuration);
		out.writeInt(explosions.size());
		for (FireworkExplosion f : explosions) {
			f.toBytes(out);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		flightDuration = in.readVarInt();
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			FireworkExplosion e = new FireworkExplosion();
			e.fromBytes(in);
			explosions.add(e);
		}
	}

	public FireworksComponent copy() {
		FireworksComponent c = new FireworksComponent();
		c.flightDuration = flightDuration;
		for (var f : explosions) {
			c.explosions.add(f.copy());
		}
		return c;
	}

}
