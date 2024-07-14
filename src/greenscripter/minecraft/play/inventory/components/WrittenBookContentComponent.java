package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.nbt.NBTComponent;
import greenscripter.minecraft.play.inventory.Component;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class WrittenBookContentComponent extends Component {

	public static final int componentId = ComponentData.get("minecraft:written_book_content");

	public String rawTitle;
	public String filteredTitle;
	public String author;
	public int generation;
	public List<Page> pages = new ArrayList<>();
	public boolean resolved;

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeString(rawTitle);
		out.writeBoolean(filteredTitle != null);
		if (filteredTitle != null) out.writeString(filteredTitle);
		out.writeString(author);
		out.writeVarInt(generation);
		out.writeVarInt(pages.size());
		for (Page e : pages) {
			e.toBytes(out);
		}
		out.writeBoolean(resolved);
	}

	public void fromBytes(MCInputStream in) throws IOException {
		rawTitle = in.readString();
		if (in.readBoolean()) {
			filteredTitle = in.readString();
		}
		author = in.readString();
		generation = in.readVarInt();
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			Page e = new Page();
			e.fromBytes(in);
			pages.add(e);
		}
		resolved = in.readBoolean();
	}

	public WrittenBookContentComponent copy() {
		WrittenBookContentComponent c = new WrittenBookContentComponent();
		c.rawTitle = rawTitle;
		c.author = author;
		c.generation = generation;
		c.filteredTitle = filteredTitle;
		c.resolved = resolved;
		for (Page e : pages) {
			c.pages.add(e.copy());
		}
		return c;
	}

	public static class Page {

		public NBTComponent content;
		public NBTComponent filteredContent;

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeNBT(content);
			out.writeBoolean(filteredContent != null);
			if (filteredContent != null) out.writeNBT(filteredContent);
		}

		public void fromBytes(MCInputStream in) throws IOException {
			content = in.readNBT();
			if (in.readBoolean()) filteredContent = in.readNBT();
		}

		public Page copy() {
			Page e = new Page();
			e.content = content.copy();
			if (filteredContent != null) e.filteredContent = filteredContent.copy();
			return e;
		}
	}

}
