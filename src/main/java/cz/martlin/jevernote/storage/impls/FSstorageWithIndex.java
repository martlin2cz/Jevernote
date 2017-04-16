package cz.martlin.jevernote.storage.impls;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.misc.Log;

public abstract class FSstorageWithIndex extends BaseFileSystemStorage {

	private Map<String, File> bindings;
	private boolean indexChanged;

	public FSstorageWithIndex(File basePath) {
		super(basePath);
	}

	public Map<String, File> getBindings() {
		return bindings;
	}
	
	@Override
	public void initialize(String storageDesc) throws JevernoteException {
		initializeBindingsStorage(storageDesc);
	}

	protected abstract Map<String, File> initializeBindingsStorage(String storageDesc) throws JevernoteException;

	
	///////////////////////////////////////////////////////////////////////////

	
	@Override
	protected void doLoad() throws JevernoteException {
		this.bindings = loadBindings();
	}

	protected abstract Map<String, File> loadBindings() throws JevernoteException;

	@Override
	protected void doStore() throws JevernoteException {
		if (requiresSave()) {
			saveBindings(bindings);
		}
	}

	protected abstract void saveBindings(Map<String, File> bindings) throws JevernoteException;

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void createPackage(Package pack) throws JevernoteException {
		super.createPackage(pack);

		createPackageInIndex(pack);
	}

	@Override
	public void createItem(Item item) throws JevernoteException {
		super.createItem(item);

		createItemInIndex(item);
	}

	@Override
	public void movePackage(Package oldPack, Package newPack) throws JevernoteException {
		super.movePackage(oldPack, newPack);

		movePackageInIndex(oldPack, newPack);
	}

	@Override
	public void moveItem(Item oldItem, Item newItem) throws JevernoteException {
		super.moveItem(oldItem, newItem);

		moveItemInIndex(newItem);
	}

	@Override
	public void updateItem(Item item) throws JevernoteException {
		super.updateItem(item);

		// nothing needed here
	}

	@Override
	public void removePackage(Package pack) throws JevernoteException {
		super.removePackage(pack);

		removePackageFromIndex(pack);
	}

	@Override
	public void removeItem(Item item) throws JevernoteException {
		super.removeItem(item);

		removeItemFromIndex(item);
	}

	///////////////////////////////////////////////////////////////////////////

	private void createPackageInIndex(Package pack) {
		String id = checkPackageId(pack, "Creating");
		File dir = packageToNative(pack);

		bindings.put(id, dir);

		markChanged();
	}

	private void createItemInIndex(Item item) {
		String id = checkItemId(item, "Creating");
		File file = itemToNative(item);

		bindings.put(id, file);

		markChanged();
	}

	private void movePackageInIndex(Package oldPack, Package newPack) {
		File newDir = packageToNative(newPack);
		File oldDir = packageToNative(oldPack);

		String id = checkPackageId(newPack, "Moving");
		bindings.put(id, newDir);

		renameItemsOfPackage(oldDir, newDir, newPack);

		markChanged();
	}

	private void moveItemInIndex(Item newItem) {
		String id = checkItemId(newItem, "Moving");
		File newFile = itemToNative(newItem);
		bindings.put(id, newFile);

		markChanged();
	}

	private void removePackageFromIndex(Package pack) {
		String id = checkPackageId(pack, "Removing");
		bindings.remove(id);

		markChanged();
	}

	private void removeItemFromIndex(Item item) {
		String id = checkItemId(item, "Removing");
		bindings.remove(id);

		markChanged();
	}

	private void renameItemsOfPackage(File oldDir, File newDir, Package pack) {
		String oldName = oldDir.getName();

		bindings.forEach((id, file) -> {
			String fileDirName = file.getParentFile().getName();

			if (oldName.equals(fileDirName)) {
				String name = file.getName();
				File newFile = nameToItemFile(pack, name);
				bindings.put(id, newFile);
			}
		});
	}

	///////////////////////////////////////////////////////////////////////////

	private String checkPackageId(Package pack, String operation) {
		String id = pack.getId();

		if (id == null) {
			id = createId();
			pack.setId(id);
			Log.warn(operation + " package with no id");
		}

		return id;
	}

	private String checkItemId(Item item, String operation) {
		String id = item.getId();

		if (id == null) {
			id = createId();
			item.setId(id);
			Log.warn(operation + " item with no id");
		}

		return id;
	}

	private String createId() {
		return "Undefined-identifier-" + System.currentTimeMillis() + "-" + System.nanoTime();
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected String findIdOfItem(File file) {
		String id = findKey(file, bindings);
		if (id != null) {
			return id;
		} else {
			Log.warn("No record for item " + packOrItemToPath(file) + " in index file");
			return createId();
		}
	}

	@Override
	protected String findIdOfPack(File dir) {
		String id = findKey(dir, bindings);
		if (id != null) {
			return id;
		} else {
			Log.warn("No record for package " + packOrItemToPath(dir) + " in index file");
			return createId();
		}
	}

	@Override
	protected File findPackageDirById(String id) {
		File dir = bindings.get(id);
		if (dir != null) {
			return dir;
		} else {
			throw new IllegalArgumentException("Package with id " + id + " seems does not exist");
		}
	}

	@Override
	protected File findItemFileById(String id) {
		File file = bindings.get(id);
		if (file != null) {
			return file;
		} else {
			throw new IllegalArgumentException("Item with id " + id + " seems does not exist");
		}
	}

	///////////////////////////////////////////////////////////////////////////

	private void markChanged() {
		this.indexChanged = true;
	}

	public boolean requiresSave() {
		return this.indexChanged;
	}

	///////////////////////////////////////////////////////////////////////////

	public static <K, V> K findKey(V value, Map<K, V> map) {

		for (Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}

		return null;
	}

}
