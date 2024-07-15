package greenscripter.minecraft.play.inventory;

import java.util.ArrayList;
import java.util.List;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import greenscripter.minecraft.gameinfo.ComponentData;
import greenscripter.minecraft.utils.MCInputStream;
import greenscripter.minecraft.utils.MCOutputStream;
import greenscripter.minecraft.utils.Pair;

public class Components {

	//Simple component tests
	public static void main(String[] args) {
		Slot slot = new Slot();
		slot.itemCount = 5;
		slot.itemId = ItemId.get("minecraft:dirt");
		System.out.println(slot);
		System.out.println(ComponentData.componentTypes.size());

		for (var c : ComponentData.componentTypes.entrySet().stream().map(s -> new Pair<>(s.getKey(), s.getValue().get())).toList()) {
			if (c.getU().intValue() != c.getV().id()) {
				System.out.println("Incorrect component id " + c.getU() + " != " + c.getV().id());
			}
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			try {
				c.v.toBytes(new MCOutputStream(data));
			} catch (IOException e) {
				e.printStackTrace();
			}

			Component a = c.v;
			Component b = c.v.copy();
			if (!a.equals(b)) {
				System.out.println("    " + a);
				System.out.println("    " + b);
				System.out.println("        " + false);
			}

			try {
				Component d = ComponentData.getComponent(c.u);
				d.fromBytes(new MCInputStream(new ByteArrayInputStream(data.toByteArray())));
				if (!d.equals(a)) {
					System.out.println("    " + d);
					System.out.println("    " + a);
					System.out.println("        " + false);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// Check all default item components.
		for (var entry : ComponentData.defaultComponents.entrySet()) {
			Components cs = entry.getValue();
			if (!cs.equals(cs.copy())) {
				System.out.println(ItemId.get(entry.getKey()));
				System.out.println("    " + cs);
				System.out.println("    " + cs.copy());
				System.out.println("        " + cs.equals(cs.copy()));
			}
		}
	}

	public int itemId;
	Component[] components = new Component[64];

	public Components() {
		itemId = -1;
	}

	public Components(int item) {
		itemId = item;
	}

	public Component getComponent(int id) {
		Component component = components[id];
		if (component == REMOVED) {
			return null;
		}
		if (component == null) {
			return getDefaultComponent(id);
		}
		return component;
	}

	public boolean contains(Component c) {
		return c.equals(getComponent(c.id()));
	}

	public boolean containsAll(Components c) {
		return getComponents().containsAll(c.getComponents());
	}

	public void removeComponent(int id) {
		components[id] = REMOVED;
	}

	public void clearComponent(int id) {
		components[id] = null;
	}

	public void setComponent(Component c) {
		if (!c.equals(getComponent(c.id()))) {
			components[c.id()] = c;
		}
	}

	public boolean isComponentRemoved(int id) {
		if (components[id] == REMOVED) {
			return doesComponentHaveDefault(id);
		}
		return false;
	}

	public List<Component> getComponents() {
		List<Component> result = new ArrayList<>();

		for (int i = 0; i < components.length; i++) {
			Component next = getComponent(i);
			if (next != null) result.add(next);
		}

		return result;
	}

	public List<Integer> getRemovedComponents() {
		List<Integer> result = new ArrayList<>();

		for (int i = 0; i < components.length; i++) {
			if (isComponentRemoved(i)) {
				result.add(i);
			}
		}

		return result;
	}

	public List<Component> getAddedComponents() {
		List<Component> result = new ArrayList<>();

		for (int i = 0; i < components.length; i++) {
			if (components[i] != null && components[i] != REMOVED) {
				result.add(components[i]);
			}
		}

		return result;
	}

	public boolean doesComponentHaveDefault(int id) {
		Components defaults = getDefaultComponents();
		if (defaults == null) return false;
		return defaults.components[id] != null;
	}

	public Components getDefaultComponents() {
		return ComponentData.defaultComponents.get(itemId);
	}

	public Component getDefaultComponent(int id) {
		Components defaults = getDefaultComponents();
		if (defaults == null) return null;
		Component d = defaults.components[id];
		if (d != null) d = d.copy();
		return d;
	}

	public static Component readComponent(MCInputStream in) throws IOException {
		int id = in.readVarInt();
		Component c = ComponentData.getComponent(id);
		c.fromBytes(in);
		return c;
	}

	public static void writeComponent(MCOutputStream out, Component c) throws IOException {
		out.writeVarInt(c.id());
		c.toBytes(out);
	}

	public Components copy() {
		Components other = new Components();
		other.itemId = itemId;
		for (int i = 0; i < components.length; i++) {
			if (components[i] != null) {
				other.components[i] = components[i].copy();
			}
		}
		return other;
	}

	public String toString() {
		return toString(true);
	}

	public String toString(boolean includeDefaults) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		boolean any = false;
		for (int i = 0; i < components.length; i++) {
			if (isComponentRemoved(i)) {
				sb.append("Removed ");
				sb.append(ComponentData.get(i));
				sb.append(", ");
				any = true;
			} else if (components[i] != null && components[i] != REMOVED) {
				sb.append(components[i]);
				sb.append(", ");
				any = true;
			} else if (includeDefaults) {

				Component def = getDefaultComponent(i);

				if (def != null) {
					sb.append("Default: ");
					sb.append(def);
					sb.append(", ");
					any = true;
				}
			}
		}

		if (any) {
			sb.setLength(sb.length() - 2);
		}
		sb.append("]");
		return sb.toString();
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getComponents().hashCode();
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Components other = (Components) obj;
		return getComponents().equals(other.getComponents());
	}

	public static final Component REMOVED = new Component() {

		public void toBytes(MCOutputStream out) throws IOException {
			throw new UnsupportedOperationException();
		}

		public int id() {
			return -1;
		}

		public void fromBytes(MCInputStream in) throws IOException {
			throw new UnsupportedOperationException();
		}

		public Component copy() {
			return this;
		}
	};

}
