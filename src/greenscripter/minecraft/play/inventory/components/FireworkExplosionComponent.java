package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class FireworkExplosionComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:firework_explosion");

	public FireworkExplosion explosion;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		explosion.toBytes(out);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		explosion = new FireworkExplosion();
		explosion.fromBytes(in);
	}

	public FireworkExplosionComponent copy() {
		FireworkExplosionComponent c = new FireworkExplosionComponent();
		if (explosion != null) c.explosion = explosion.copy();
		return c;
	}

}
