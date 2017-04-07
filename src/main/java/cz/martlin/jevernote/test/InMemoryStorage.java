package cz.martlin.jevernote.test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.martlin.jevernote.dataobj.Item;
import cz.martlin.jevernote.dataobj.Package;
import cz.martlin.jevernote.impls.CommonStorage;

public class InMemoryStorage extends CommonStorage<Package, Item> {

	private final Map<Package, List<Item>> storage;

	public InMemoryStorage() {
		this.storage = new HashMap<>();
	}
	
	public void initialize(Map<Package, List<Item>> data) {
		this.storage.putAll(data);
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
	protected void createNativeItem(Item item, Item nativ) throws Exception {
		Package pack = item.getPack();
		List<Item> items = storage.get(pack);
		items.add(nativ);
	}

	@Override
	protected void updatePackageNative(Package pack, Package nativ) throws Exception {
		// yet done
	}

	@Override
	protected void updateNativeItem(Item item, Item nativ) throws Exception {
		// yet done

	}

	@Override
	protected void removePackageNative(Package pack, Package nativ) throws Exception {
		storage.remove(nativ);
	}

	@Override
	protected void removeNativeItem(Item item, Item nativ) throws Exception {
		Package pack = item.getPack();
		List<Item> items = storage.get(pack);
		items.remove(nativ);
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
		for (Package pack: storage.keySet()) {
			out.println(pack.getName() + " (" + pack.getId() + "):");
			
			List<Item> items = storage.get(pack);
			for (Item item: items) {
				out.println(" - "+ item.getName() + " (" + item.getId() + "), " + item.getLastModifiedAt().getTime() + ":");
				out.println("    " + item.getContent());
			}
		}
		
	}
	
}
