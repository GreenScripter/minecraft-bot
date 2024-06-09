package greenscripter.minecraft.play.inventory;

import java.util.Set;
import java.util.function.Predicate;

public class ItemUtils {

	public static Predicate<Slot> presentSlots(Predicate<Slot> p) {
		return s -> s.present && p.test(s);
	}

	private static final Predicate<Slot> present = s -> s.present;

	public static Predicate<Slot> presentSlots() {
		return present;
	}

	public static Predicate<Slot> matchesTag(String tag) {
		Set<Integer> ids = ItemId.tags(tag);
		return s -> s.present && ids.contains(s.itemId);
	}

	public static Predicate<Slot> matchesId(String id) {
		int idi = ItemId.get(id);
		return s -> s.present && idi == s.itemId;
	}

	public static Predicate<Slot> matchesId(int id) {
		return s -> s.present && id == s.itemId;
	}

	public static Slot slot(int type, int count) {
		Slot s = new Slot();
		s.present = count > 0;
		s.itemId = type;
		s.itemCount = (byte) count;
		return s;
	}

}
