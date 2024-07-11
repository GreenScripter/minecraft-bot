package greenscripter.minecraft.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import greenscripter.minecraft.nbt.NBTComponent;

public class DynamicRegistry {

	public String name;
	public RegistryEntry[] registry = {};
	public Map<String, RegistryEntry> reversed = new HashMap<>();
	
	public RegistryEntry get(int id) {
		return registry[id];
	}
	
	public RegistryEntry get(String id) {
		return reversed.get(id);
	}

	public static class RegistryEntry {

		public int id;
		public String entryId;
		public boolean hasData;
		public NBTComponent data;

		public String toString() {
			return "RegistryEntry [id=" + id + ", " + (entryId != null ? "entryId=" + entryId + ", " : "") + "hasData=" + hasData + ", " + (data != null ? "data=" + data : "") + "]";
		}
	}

	public String toString() {
		return "DynamicRegistry [" + (name != null ? "name=" + name + ", " : "") + (registry != null ? "registry=" + Arrays.toString(registry) + ", " : "") + (reversed != null ? "reversed=" + reversed : "") + "]";
	}

}
