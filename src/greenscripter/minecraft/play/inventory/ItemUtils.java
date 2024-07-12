package greenscripter.minecraft.play.inventory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

	public static Predicate<Slot> matchesIds(String... ids) {
		int[] intIds = new int[ids.length];
		for (int i = 0; i < ids.length; i++) {
			intIds[i] = ItemId.get(ids[i]);
		}
		return matchesIds(intIds);
	}

	public static Predicate<Slot> matchesIds(int... ids) {
		HashSet<Integer> idSet = new HashSet<>();
		for (int id : ids) {
			idSet.add(id);
		}
		return s -> s.present && idSet.contains(s.itemId);
	}

	public static Slot slot(int type, int count) {
		Slot s = new Slot();
		s.present = count > 0;
		s.itemId = type;
		s.itemCount = count;
		return s;
	}

	public static int countItems(Iterator<Slot> slots) {
		int count = 0;
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.present) count += slot.itemCount;
		}
		return count;
	}

	public static int countItems(String type, Iterator<Slot> slots) {
		return countItems(ItemId.get(type), slots);
	}

	public static int countItems(int type, Iterator<Slot> slots) {
		int count = 0;
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.present && slot.itemId == type) count += slot.itemCount;
		}
		return count;
	}

	public static int countItems(Slot type, Iterator<Slot> slots) {
		int count = 0;
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.equivalent(type)) count += slot.itemCount;
		}
		return count;
	}

	public static int countItems(Predicate<Slot> type, Iterator<Slot> slots) {
		int count = 0;
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.present && type.test(slot)) count += slot.itemCount;
		}
		return count;
	}

	public static int countSpaceForItem(int type, Iterator<Slot> slots) {
		int count = 0;
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.present && slot.itemId == type) {
				count += slot.getItemInfo().maxStack - slot.itemCount;
			} else if (!slot.present) {
				count += ItemId.info(type).maxStack;
			}
		}
		return count;
	}

	public static int countSpaceForItem(Slot type, Iterator<Slot> slots) {
		int count = 0;
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.equivalent(type)) {
				count += slot.getItemInfo().maxStack - slot.itemCount;
			} else if (!slot.present) {
				count += type.getItemInfo().maxStack;
			}
		}
		return count;
	}

	public static int countSlotsWithItem(String type, Iterator<Slot> slots) {
		return countSlotsWithItem(ItemId.get(type), slots);
	}

	public static int countSlotsWithItem(int type, Iterator<Slot> slots) {
		int count = 0;
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.present && slot.itemId == type) count++;
		}
		return count;
	}

	public static int countSlotsWithItem(Slot type, Iterator<Slot> slots) {
		int count = 0;
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.equivalent(type)) count++;
		}
		return count;
	}

	public static int countSlotsMatching(Predicate<Slot> type, Iterator<Slot> slots) {
		int count = 0;
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (type.test(slot)) count++;
		}
		return count;
	}

	public static int countEmptySlots(Iterator<Slot> slots) {
		int count = 0;
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (!slot.present) count++;
		}
		return count;
	}

	public static int countSpaceInUsedSlots(int type, Iterator<Slot> slots) {
		int count = 0;
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.present && slot.itemId == type) count += slot.getItemInfo().maxStack - slot.itemCount;
		}
		return count;
	}

	public static int countSpaceInUsedSlots(Slot type, Iterator<Slot> slots) {
		int count = 0;
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.equivalent(type)) count += slot.getItemInfo().maxStack - slot.itemCount;
		}
		return count;
	}

	public static int countUsedSlots(Iterator<Slot> slots) {
		int count = 0;
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.present) count++;
		}
		return count;
	}

	public static List<Slot> getItems(Iterator<Slot> slots) {
		List<Slot> result = new ArrayList<>();
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.present) result.add(slot);
		}
		return result;
	}

	public static List<Slot> getSlotsWithItem(String type, Iterator<Slot> slots) {
		return getSlotsWithItem(ItemId.get(type), slots);
	}

	public static List<Slot> getSlotsWithItem(int type, Iterator<Slot> slots) {
		List<Slot> result = new ArrayList<>();
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.present && slot.itemId == type) result.add(slot);
		}
		return result;
	}

	public static List<Slot> getSlotsWithItem(Slot type, Iterator<Slot> slots) {
		List<Slot> result = new ArrayList<>();
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.equivalent(type)) result.add(slot);
		}
		return result;
	}

	public static List<Slot> getSlotsMatching(Predicate<Slot> type, Iterator<Slot> slots) {
		List<Slot> result = new ArrayList<>();
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (type.test(slot)) result.add(slot);
		}
		return result;
	}

	public static List<Slot> getSlotsFittingItem(int type, Iterator<Slot> slots) {
		List<Slot> result = new ArrayList<>();
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.present && slot.itemId == type && slot.getItemInfo().maxStack > slot.itemCount)
				result.add(slot);
			else if (!slot.present) result.add(slot);

		}
		return result;
	}

	public static List<Slot> getSlotsFittingItem(Slot type, Iterator<Slot> slots) {
		List<Slot> result = new ArrayList<>();
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.equivalent(type) && slot.getItemInfo().maxStack - slot.itemCount >= type.itemCount)
				result.add(slot);
			else if (!slot.present) result.add(slot);
		}
		return result;
	}

	public static List<Slot> getEmptySlots(Iterator<Slot> slots) {
		List<Slot> result = new ArrayList<>();
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (!slot.present) result.add(slot);
		}
		return result;
	}

	public static List<Slot> getUsedSlots(Iterator<Slot> slots) {
		List<Slot> result = new ArrayList<>();
		while (slots.hasNext()) {
			Slot slot = slots.next();
			if (slot.present) result.add(slot);
		}
		return result;
	}
}
