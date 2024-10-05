package greenscripter.minecraft.play.inventory;

import java.util.Objects;

import greenscripter.minecraft.gameinfo.Registries.ItemInfo;

public class Slot {

	public boolean present;
	public int itemId;
	public int itemCount;
	private Components components = new Components();

	public String toString() {
		return "Slot [present=" + present + ", itemId=" + itemId + (present ? " " + getItemId() : "") + ", itemCount=" + itemCount + ", components=" + getComponents() + "]";
	}

	public String toStringShort() {
		if (present) {
			return itemCount + " " + getItemId() + (components != null ? " " + getComponents().toString(false) : "");
		} else {
			return "empty";
		}
	}

	public void become(Slot other) {
		if (other == null) return;
		this.present = other.present;
		this.itemId = other.itemId;
		this.itemCount = other.itemCount;
		this.components = other.components;
		if (this.components != null) {
			components = components.copy();
		} else {
			components = new Components();
		}
	}

	public void add(int count) {
		itemCount += count;
		if (itemCount <= 0) {
			present = false;
			itemId = 0;
			itemCount = 0;
			components = new Components();
		}
	}

	public void setCount(int count) {
		itemCount = (byte) count;
		if (itemCount <= 0) {
			present = false;
			itemId = 0;
			itemCount = 0;
			components = new Components();
		}
	}

	public void setComponents(Components c) {
		components = c.copy();
		components.itemId = itemId;
	}

	public Components getComponents() {
		if (components == null) return null;
		components.itemId = itemId;
		return components;
	}

	public String getItemId() {
		if (!present) return null;
		return ItemId.itemRegistry.get(itemId);
	}

	public ItemInfo getItemInfo() {
		if (!present) return null;
		return ItemId.itemInfo.get(itemId);
	}

	public boolean equivalent(Slot other) {
		if (present == other.present && present == false) return true;
		return present == other.present && itemId == other.itemId && Objects.equals(components, other.components);
	}

	public Slot copy() {
		Slot s = new Slot();
		s.become(this);
		return s;
	}
}
