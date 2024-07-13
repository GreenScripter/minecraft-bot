package greenscripter.minecraft.play.inventory.components;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentIds;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;

public class WritableBookContentComponent extends Component {

	public static final int componentId = ComponentIds.get("minecraft:writable_book_content");

	public List<Page> pages = new ArrayList<>();

	public int id() {
		return componentId;
	}

	public void toBytes(MCOutputStream out) throws IOException {
		out.writeVarInt(pages.size());
		for (Page e : pages) {
			e.toBytes(out);
		}
	}

	public void fromBytes(MCInputStream in) throws IOException {
		int length = in.readVarInt();
		for (int i = 0; i < length; i++) {
			Page e = new Page();
			e.fromBytes(in);
			pages.add(e);
		}
	}

	public WritableBookContentComponent copy() {
		WritableBookContentComponent c = new WritableBookContentComponent();
		for (Page e : pages) {
			c.pages.add(e.copy());
		}
		return c;
	}

	public static class Page {

		public String content;
		public String filteredContent;

		public void toBytes(MCOutputStream out) throws IOException {
			out.writeString(content);
			out.writeBoolean(filteredContent != null);
			if (filteredContent != null) out.writeString(filteredContent);
		}

		public void fromBytes(MCInputStream in) throws IOException {
			content = in.readString();
			if (in.readBoolean()) filteredContent = in.readString();
		}

		public Page copy() {
			Page e = new Page();
			e.content = content;
			e.filteredContent = filteredContent;
			return e;
		}
	}

}
