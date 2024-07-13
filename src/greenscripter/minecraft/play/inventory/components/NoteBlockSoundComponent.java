package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class NoteBlockSoundComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:note_block_sound");

	public String sound;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeString(sound);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		sound = in.readString();
	}

	public NoteBlockSoundComponent copy() {
		NoteBlockSoundComponent c = new NoteBlockSoundComponent();
		c.sound = sound;
		return c;
	}

}
