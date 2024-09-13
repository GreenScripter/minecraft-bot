package greenscripter.minecraft.world.entity.metadata;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.world.entity.EntityMetadata;

public class EMParticleList extends EntityMetadata {

	public List<EMParticle> particles = new ArrayList<>();

	public int id() {
		return 18;
	}

	public void read(MCInputStream in) throws IOException {
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			EMParticle p = new EMParticle();
			p.read(in);
			particles.add(p);
		}
	}

	public void write(MCOutputStream out) throws IOException {
		out.writeVarInt(particles.size());
		for (EMParticle p : particles) {
			p.write(out);
		}
	}

	public String toString() {
		return "EMParticleList [" + (particles != null ? "particles=" + particles : "") + "]";
	}

}
