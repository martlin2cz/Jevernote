package cz.martlin.jevernote.storage.impls;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.jevernote.dataobj.misc.Config;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.misc.JevernoteException;

public abstract class FSstorageWithIndex extends BaseFileSystemStorage {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private Map<String, File> bindings;
	private boolean indexChanged;

	public FSstorageWithIndex(Config config, File basePath) {
		super(config, basePath);
	}

	public Map<String, File> getBindings() {
		return bindings;
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public boolean doIsFSInstalled() throws IOException {
		return checkBindingsExistence();
	}

	@Override
	protected void doFSInstallAndLoad(String installData) throws IOException {
		bindings = initializeBindingsStorage(installData);
	}

	@Override
	protected void doFSLoad() throws IOException {
		bindings = loadBindings();
	}

	@Override
	protected void doFSStore() throws IOException {
		if (requiresSave()) {
			saveBindings(bindings);
		}
	}

	protected abstract boolean checkBindingsExistence();

	protected abstract Map<String, File> initializeBindingsStorage(String storageDesc) throws IOException;

	protected abstract Map<String, File> loadBindings() throws IOException;

	protected abstract void saveBindings(Map<String, File> bindings) throws IOException;

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void doCreatePackage(Package pack) throws JevernoteException {
		super.doCreatePackage(pack);

		createPackageInIndex(pack);
	}

	@Override
	public void doCreateItem(Item item) throws JevernoteException {
		super.doCreateItem(item);

		createItemInIndex(item);
	}

	@Override
	public void doMovePackage(Package oldPack, Package newPack) throws JevernoteException {
		super.doMovePackage(oldPack, newPack);

		movePackageInIndex(oldPack, newPack);
	}

	@Override
	public void doMoveItem(Item oldItem, Item newItem) throws JevernoteException {
		super.doMoveItem(oldItem, newItem);

		moveItemInIndex(newItem);
	}

	@Override
	public void doUpdateItem(Item item) throws JevernoteException {
		super.doUpdateItem(item);

		// nothing needed here
	}

	@Override
	public void doRemovePackage(Package pack) throws JevernoteException {
		super.doRemovePackage(pack);

		removePackageFromIndex(pack);
	}

	@Override
	public void doRemoveItem(Item item) throws JevernoteException {
		super.doRemoveItem(item);

		removeItemFromIndex(item);
	}

	@Override
	public void doBackupPackage(Package pack) throws JevernoteException {
		super.doBackupPackage(pack);
		// okay
	}

	@Override
	public void doBackupItem(Item item) throws JevernoteException {
		super.doBackupItem(item);
		// okay
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
			LOG.warn(operation + " package with no id");
		}

		return id;
	}

	private String checkItemId(Item item, String operation) {
		String id = item.getId();

		if (id == null) {
			id = createId();
			item.setId(id);
			LOG.warn(operation + " item with no id");
		}

		return id;
	}

	public static String createId() {
		return "Undefined-identifier-" + System.currentTimeMillis() + "-" + System.nanoTime();
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected String findIdOfItem(File file) {
		String id = findKey(file, bindings);
		if (id != null) {
			return id;
		} else {
			LOG.warn("No record for item " + packOrItemToPath(file) + " in index file");
			return createId();
		}
	}

	@Override
	protected String findIdOfPack(File dir) {
		String id = findKey(dir, bindings);
		if (id != null) {
			return id;
		} else {
			LOG.warn("No record for package " + packOrItemToPath(dir) + " in index file");
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
