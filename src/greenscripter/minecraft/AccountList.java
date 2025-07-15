package greenscripter.minecraft;

import java.util.ArrayList;
import java.util.UUID;

import greenscripter.minecraft.AccountList.Account;

public class AccountList extends ArrayList<Account> {

	public void add(String name, String uuid) {
		this.add(new Account(name, uuid));
	}

	public String getName(int i) {
		return get(i).name;
	}

	public UUID getUUID(int i) {
		return UUID.fromString(get(i).uuid);
	}

	public UUID getUUID(String name) {
		return this.stream().filter(a -> a.name.equals(name)).map(a -> UUID.fromString(a.uuid)).findAny().orElse(null);
	}

	public record Account(String name, String uuid) {}
}
