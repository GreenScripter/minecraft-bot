package greenscripter.minecraft.play.inventory.components;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class JukeboxPlayableComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:jukebox_playable");

	public boolean directMode;
	public String songName;

	public int songType;
	public SoundEvent soundEvent;
	public NBTComponent description;
	public float duration;
	public int output;

	public boolean showInTooltip = true;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeBoolean(directMode);
		if (directMode) {
			out.writeVarInt(songType);
			soundEvent.toBytes(out);
			out.writeNBT(description);
			out.writeFloat(duration);
			out.writeVarInt(output);
		} else {
			out.writeString(songName);
		}

		out.writeBoolean(showInTooltip);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		directMode = in.readBoolean();
		if (directMode) {
			songType = in.readVarInt();
			soundEvent = new SoundEvent();
			soundEvent.fromBytes(in);
			description = in.readNBT();
			duration = in.readFloat();
			output = in.readVarInt();
		} else {
			songName = in.readString();
		}
		showInTooltip = in.readBoolean();
	}

	public JukeboxPlayableComponent copy() {
		JukeboxPlayableComponent c = new JukeboxPlayableComponent();
		c.directMode = directMode;
		if (soundEvent != null) c.soundEvent = soundEvent.copy();
		if (description != null) c.description = description.copy();
		c.songType = songType;
		c.duration = duration;
		c.output = output;
		c.songName = songName;
		c.showInTooltip = showInTooltip;
		return c;
	}

}
