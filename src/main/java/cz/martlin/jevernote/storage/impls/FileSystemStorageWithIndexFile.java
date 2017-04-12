package cz.martlin.jevernote.storage.impls;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.misc.Log;

public class FileSystemStorageWithIndexFile extends BaseFileSystemStorage {

	public static final String INDEX_FILE_NAME = ".index.properties";
	private static final String COMMENT = "Jevernote index file (mapping betweeen names and ids of packages and items)";

	private final Map<String, File> bindings;
	private boolean changed;

	public FileSystemStorageWithIndexFile(File basePath) throws JevernoteException {
		super(basePath);

		this.bindings = loadBindings();
	}

	protected Map<String, File> getBindings() {
		return bindings;
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

	@Override
	protected Package nativeToPackage(File dir) throws IOException {
		Package pack = super.nativeToPackage(dir);

		String id = findIdOfPack(dir);
		pack.setId(id);

		return pack;
	}

	@Override
	protected Item nativeToItem(Package pack, File file) throws IOException {
		Item item = super.nativeToItem(pack, file);

		String id = findIdOfItem(file);
		item.setId(id);

		return item;
	}

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

	private void markChanged() {
		this.changed = true;
	}

	public void checkAndSaveChanges() throws JevernoteException {
		if (this.changed) {
			saveChangesInIndex();
		}
	}

	private void saveChangesInIndex() throws JevernoteException {
		saveBindings();
	}

	public void reloadChangesInIndex() throws JevernoteException {
		Map<String, File> loaded = loadBindings();

		this.bindings.clear();
		this.bindings.putAll(loaded);
		this.changed = false;
	}

	///////////////////////////////////////////////////////////////////////////

	public static boolean hasIndexFile(File basePath) {
		File file = indexFile(basePath);
		return file.exists() && file.isFile();
	}

	public static void createIndexFile(File basePath) throws JevernoteException {
		File file = indexFile(basePath);

		try {
			file.createNewFile(); // TODO make it with some content
		} catch (IOException e) {
			throw new JevernoteException("Cannot create file", e);
		}
	}

	private static File indexFile(File basePath) {
		return new File(basePath, INDEX_FILE_NAME);
	}

	///////////////////////////////////////////////////////////////////////////

	protected Map<String, File> loadBindings() throws JevernoteException {
		Properties props = loadProperties();
		Map<String, File> map = toMap(props);

		return map;
	}

	private Map<String, File> toMap(Properties props) {
		Map<String, File> result = new HashMap<>(props.size());

		props.forEach((k, v) -> {
			String id = (String) k;
			String path = (String) v;

			result.put(id, new File(basePath, path));
		});

		return result;
	}

	private Properties loadProperties() throws JevernoteException {
		File file = indexFile(basePath);

		Properties props = new Properties();

		Reader r = null;
		try {
			r = new FileReader(file);
			props.load(r);
		} catch (IOException e) {
			throw new JevernoteException("Cannot read index file", e);
		} finally {
			closeQuietly(r);
		}

		return props;
	}

	///////////////////////////////////////////////////////////////////////////

	protected void saveBindings() throws JevernoteException {
		Properties props = toProperties();
		saveProperties(props);

	}

	private Properties toProperties() {
		Properties props = new Properties();

		bindings.forEach((id, file) -> {
			String path = packOrItemToPath(file);
			if (path != null) {
				props.put(id, path);
			}
		});

		return props;
	}

	private void saveProperties(Properties props) throws JevernoteException {
		File file = indexFile(basePath);

		Writer w = null;
		try {
			w = new FileWriter(file);
			props.store(w, COMMENT);
		} catch (IOException e) {
			throw new JevernoteException("Cannot write index file", e);
		} finally {
			closeQuietly(w);
		}
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

	private void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// ignore, or not?
			}
		}
	}

	public static <K, V> K findKey(V value, Map<K, V> map) {

		for (Entry<K, V> entry : map.entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}

		return null;
	}

	///////////////////////////////////////////////////////////////////////////

}
