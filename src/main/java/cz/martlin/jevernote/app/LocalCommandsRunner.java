package cz.martlin.jevernote.app;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.dataobj.storage.StorageData;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.misc.RequiresLoad;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.base.StorageRequiringLoad;
import cz.martlin.jevernote.storage.impls.FSstorageWithIndex;
import cz.martlin.jevernote.storage.impls.LoggingStorageWrapper;

public class LocalCommandsRunner implements RequiresLoad<String> {
	private static final String INITIAL_CONTENT = "Hello world!";

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private final BaseStorage local;
	private final BaseStorage wrapped;
	private boolean loaded;

	public LocalCommandsRunner(BaseStorage local) {
		super();
		this.local = local;
		this.wrapped = wrapStorage(local);
	}

	private BaseStorage wrapStorage(BaseStorage storage) {
		storage = new LoggingStorageWrapper(storage, "in local");

		return storage;
	}

	@Override
	public void installAndLoad(String installData) throws JevernoteException {
		throw new UnsupportedOperationException("Assuming yet installed.");
	}

	@Override
	public boolean isInstalled() {
		try {
			return StorageRequiringLoad.isInstalled(local);
		} catch (JevernoteException e) {
			LOG.error("Cannot find out if is installed.", e);
			return false;
		}
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void load() {
		try {
			StorageRequiringLoad.loadIfRequired(local);
			loaded = true;
		} catch (JevernoteException e) {
			LOG.error("Cannot load.", e);
		}

	}

	@Override
	public void store() {
		try {
			StorageRequiringLoad.storeIfRequired(local);
			loaded = false;
		} catch (JevernoteException e) {
			LOG.error("Cannot store.", e);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	public boolean adCmd(String packOrItem) {
		LOG.debug("Running command ad with " + packOrItem);
		try {
			String content = null;
			if (isItem(packOrItem)) {
				content = tryGetItemsContent(packOrItem);
			}
			
			doMk(packOrItem, content);
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command ad failed.", e);
			return false;
		}
	}



	public boolean mkCmd(String packOrItem, String content) {
		LOG.debug("Running command mk with " + packOrItem + " and initial content " + content);
		try {
			doMk(packOrItem, content);
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command mk failed.", e);
			return false;
		}
	}

	public boolean mvCmd(String oldPackOrItem, String newPackOrItem) {
		LOG.debug("Running command mv with " + oldPackOrItem + " and " + newPackOrItem);
		try {
			doMv(oldPackOrItem, newPackOrItem);
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command mk failed.", e);
			return false;
		}
	}

	public boolean rmCmd(String packOrItem) {
		LOG.debug("Running command rm with " + packOrItem);
		try {
			doRm(packOrItem);
			return true;
		} catch (JevernoteException e) {
			LOG.error("Command mk failed.", e);
			return false;
		}
	}

	///////////////////////////////////////////////////////////////////////////

	private void doMk(String packOrItem, String content) throws JevernoteException {
		if (isPackage(packOrItem)) {
			if (content != null) {
				LOG.warn("Creating package with specified content, ignoring");
			}

			Package pack = createNewPackage(packOrItem, null);
			wrapped.createPackage(pack);
		} else if (isItem(packOrItem)) {
			Item item = createNewItem(packOrItem, null);
			if (content != null) {
				item.setContent(content);
			} else {
				item.setContent(INITIAL_CONTENT);
			}
			wrapped.createItem(item);
		} else {
			Exception e = new IllegalArgumentException(packOrItem + " is invalid");
			throw new JevernoteException("Specify package or item", e);
		}
	}

	private void doMv(String oldPackOrItem, String newPackOrItem) throws JevernoteException {
		if (isPackage(oldPackOrItem) && isPackage(newPackOrItem)) {
			Package oldPack = createExistingPackage(oldPackOrItem);
			Package newPack = createNewPackage(newPackOrItem, oldPack.getId());
			wrapped.movePackage(oldPack, newPack);
		} else if (isItem(oldPackOrItem) && isItem(newPackOrItem)) {
			Item oldItem = createExistingItem(oldPackOrItem);
			Item newItem = createNewItem(newPackOrItem, oldItem.getId());
			wrapped.moveItem(oldItem, newItem);
		} else {
			Exception e = new IllegalArgumentException(oldPackOrItem + " or " + newPackOrItem + " is invalid");
			throw new JevernoteException("Specify both package or item", e);
		}
	}

	private void doRm(String packOrItem) throws JevernoteException {
		if (isPackage(packOrItem)) {
			Package pack = createExistingPackage(packOrItem);
			wrapped.removePackage(pack);
		} else if (isItem(packOrItem)) {
			Item item = createExistingItem(packOrItem);
			wrapped.removeItem(item);
		} else {
			Exception e = new IllegalArgumentException(packOrItem + " is invalid");
			throw new JevernoteException("Specify package or item", e);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	private boolean isPackage(String packOrItem) {
		return packOrItem.matches("[^/]+");
	}

	private boolean isItem(String packOrItem) {
		return packOrItem.matches("[^/]+/[^/]+");
	}

	private String packNameOfItemSpec(String itemSpec) {
		String parts[] = itemSpec.split("/");
		return parts[0];
	}

	private String itemNameOfItemSpec(String itemSpec) {
		String parts[] = itemSpec.split("/");
		return parts[1];
	}

	///////////////////////////////////////////////////////////////////////////

	private Package createExistingPackage(String name) throws JevernoteException {
		Package pack = findPack(name);
		if (pack == null) {
			Exception e = new IllegalArgumentException("Package " + pack + " does not exist");
			throw new JevernoteException("No such package", e);
		}

		return pack;
	}

	private Item createExistingItem(String itemSpec) throws JevernoteException {
		Item item = findItem(itemSpec);
		if (item == null) {
			Exception e = new IllegalArgumentException("Item " + itemSpec + " does not exist");
			throw new JevernoteException("No such item", e);
		}

		return item;
	}

	private Item createNewItem(String itemSpec, String id) {
		if (id == null) {
			id = FSstorageWithIndex.createId();
		}

		String packName = packNameOfItemSpec(itemSpec);
		String itemName = itemNameOfItemSpec(itemSpec);

		String packId = FSstorageWithIndex.createId();
		Package pack = new Package(packId, packName);

		Calendar lastModifiedAt = Calendar.getInstance();
		return new Item(pack, id, itemName, INITIAL_CONTENT, lastModifiedAt);
	}

	private Package createNewPackage(String name, String id) {
		if (id == null) {
			id = FSstorageWithIndex.createId();
		}

		return new Package(id, name);
	}

	///////////////////////////////////////////////////////////////////////////

	private Package findPack(String name) throws JevernoteException {
		StorageData data = local.list();
		Map<String, Package> packs = listPacks(data);
		return packs.get(name);
	}

	private Item findItem(String itemSpec) throws JevernoteException {
		String packName = packNameOfItemSpec(itemSpec);
		String itemName = itemNameOfItemSpec(itemSpec);

		return findItem(packName, itemName);
	}

	private Item findItem(String packName, String itemName) throws JevernoteException {
		StorageData data = local.list();

		Map<String, Map<String, Item>> items = listItems(data);
		Map<String, Item> subitems = items.get(packName);
		if (subitems == null) {
			return null;
		}

		return subitems.get(itemName);
	}

	private Map<String, Package> listPacks(StorageData data) {
		return data.getPackages()//
				.stream()//
				.collect(Collectors.toMap(//
						(p) -> p.getName(), //
						Function.identity()));
	}

	private Map<String, Map<String, Item>> listItems(StorageData data) {
		Map<String, Map<String, Item>> map = new HashMap<>();

		for (Item item : data.getItems()) {
			String packName = item.getPack().getName();
			String itemName = item.getName();

			Map<String, Item> subitems = map.get(packName);
			if (subitems == null) {
				subitems = new HashMap<>();
				map.put(packName, subitems);
			}

			subitems.put(itemName, item);
		}

		return map;
}
	
	private String tryGetItemsContent(String itemSpec) throws JevernoteException {
		Item item = createExistingItem(itemSpec);
		
		return item.getContent();
	}
}
