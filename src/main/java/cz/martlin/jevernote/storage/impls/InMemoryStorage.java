package cz.martlin.jevernote.storage.impls;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.storage.base.CommonStorage;

public class InMemoryStorage extends CommonStorage<Package, Item> {

	private final Map<Package, List<Item>> storage;

	private final Map<Calendar, Package> backupPackages;
	private final Map<Calendar, Item> backupItems;

	public InMemoryStorage() {
		this.storage = new HashMap<>();
		this.backupPackages = new HashMap<>();
		this.backupItems = new HashMap<>();
	}

	public void initialize(StorageData data) {
		this.storage.clear();
		this.storage.putAll(data.getData());
	}

	@Override
	public void initialize(String noDescNeeded) {
		// no initialization needed
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected List<Package> listNativePackages() throws Exception {
		Set<Package> set = storage.keySet();
		return new ArrayList<>(set);
	}

	@Override
	protected List<Item> listNativeItems(Package pack) throws Exception {
		return storage.get(pack);
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected void createPackageNative(Package pack, Package nativ) throws Exception {
		List<Item> list = new LinkedList<>();
		storage.put(nativ, list);
	}

	@Override
	protected void createItemNative(Item item, Item nativ) throws Exception {
		Package pack = item.getPack();
		List<Item> items = storage.get(pack);
		items.add(nativ);
	}
	//
	// @Override
	// protected void updatePackageNative(Package pack, Package nativ) throws
	// Exception {
	// // yet done
	// }
	//
	// @Override
	// protected void updateNativeItem(Item item, Item nativ) throws Exception {
	// // yet done
	//
	// }

	@Override
	protected void movePackageNative(Package oldPack, Package newPack, Package oldNativ, Package newNativ)
			throws Exception {

		List<Item> items = storage.remove(oldNativ);
		storage.put(newNativ, items);
	}

	@Override
	protected void moveItemNative(Item oldItem, Item newItem, Item oldNativ, Item newNativ) throws Exception {

		List<Item> oldItems = storage.get(oldNativ.getPack());
		oldItems.remove(oldNativ);

		List<Item> newItems = storage.get(newNativ.getPack());
		newItems.add(newNativ);

	}

	@Override
	protected void updateItemNative(Item item, Item nativ) throws Exception {
		List<Item> items = storage.get(nativ.getPack());

		items.remove(nativ);
		items.add(item);
	}

	@Override
	protected void removePackageNative(Package pack, Package nativ) throws Exception {
		storage.remove(nativ);
	}

	@Override
	protected void removeItemNative(Item item, Item nativ) throws Exception {
		Package pack = item.getPack();
		List<Item> items = storage.get(pack);
		items.remove(nativ);
	}

	///////////////////////////////////////////////////////////////////////////

	protected void backupPackageNative(Package pack, Package nativ) throws Exception {
		Calendar now = Calendar.getInstance();
		backupPackages.put(now, nativ);
	}

	protected void backupItemNative(Item item, Item nativ) throws Exception {
		Calendar now = Calendar.getInstance();
		backupItems.put(now, nativ);
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected Item itemToNative(Item item) throws Exception {
		return item;
	}

	@Override
	protected Item nativeToItem(Package pack, Item nativ) throws Exception {
		return nativ;
	}

	@Override
	protected Package packageToNative(Package pack) throws Exception {
		return pack;
	}

	@Override
	protected Package nativeToPackage(Package nativ) throws Exception {
		return nativ;
	}

	///////////////////////////////////////////////////////////////////////////

	public void print(PrintStream out) {
		for (Package pack : storage.keySet()) {
			out.println(pack.getName() + " (" + pack.getId() + "):");

			List<Item> items = storage.get(pack);
			for (Item item : items) {
				out.println(" - " + item.getName() + " (" + item.getId() + "), " + item.getLastModifiedAt().getTime()
						+ ":");
				out.println("    " + item.getContent());
			}
		}

		out.print("Backed up packages: ");
		for (Package pack : backupPackages.values()) {
			out.print(pack.getName());
			out.print(", ");
		}
		out.println();

		out.print("Backed up items: ");
		for (Item item : backupItems.values()) {
			out.print(item.getName());
			out.print(", ");
		}
		out.println();
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((backupItems == null) ? 0 : backupItems.hashCode());
		result = prime * result + ((backupPackages == null) ? 0 : backupPackages.hashCode());
		result = prime * result + ((storage == null) ? 0 : storage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InMemoryStorage other = (InMemoryStorage) obj;
		if (backupItems == null) {
			if (other.backupItems != null)
				return false;
		} else if (!backupItems.equals(other.backupItems))
			return false;
		if (backupPackages == null) {
			if (other.backupPackages != null)
				return false;
		} else if (!backupPackages.equals(other.backupPackages))
			return false;
		if (storage == null) {
			if (other.storage != null)
				return false;
		} else if (!storage.equals(other.storage))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InMemoryStorage [storage=" + storage + ", backupPackages=" + backupPackages + ", backupItems="
				+ backupItems + "]";
	}

}
