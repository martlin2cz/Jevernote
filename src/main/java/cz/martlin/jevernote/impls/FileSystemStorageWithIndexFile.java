package cz.martlin.jevernote.impls;

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

import cz.martlin.jevernote.core.JevernoteException;
import cz.martlin.jevernote.dataobj.Item;
import cz.martlin.jevernote.dataobj.Package;
import cz.martlin.jevernote.misc.Log;

public class FileSystemStorageWithIndexFile extends BaseFileSystemStorage {

	public static final String INDEX_FILE_NAME = ".index.properties";
	private static final String COMMENT = "Jevernote index file listing mapping betweeen names and ids of packages and items";

	private final Map<String, File> bindings;

	public FileSystemStorageWithIndexFile(File basePath, boolean loadIndex) throws JevernoteException {
		super(basePath);

		if (loadIndex) {
			this.bindings = loadBindings();
		} else {
			this.bindings = new HashMap<>();
		}
	}

	@Override
	protected String findIdOfItem(File file) {
		String id = findKey(file, bindings);
		if (id != null) {
			return id;
		} else {
			Log.warn("No record for item " + file.getAbsolutePath() + " in index file");
			// TODO absolutePath -> dir/file
			return createId();
		}
	}

	@Override
	protected String findIdOfPack(File dir) {
		String id = findKey(dir, bindings);
		if (id != null) {
			return id;
		} else {
			Log.warn("No record for package " + dir.getAbsolutePath() + " in index file");
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

	@Override
	public void createPackage(Package pack) throws JevernoteException {
		super.createPackage(pack);

		String id = pack.getId();
		if (id == null) {
			id = createId();
			pack.setId(id);
			Log.warn("Creating package with no id");
		}
		File dir = packageToNative(pack);
		bindings.put(id, dir);

		saveBindings();
	}

	@Override
	public void createItem(Item item) throws JevernoteException {
		super.createItem(item);

		String id = item.getId();
		if (id == null) {
			id = createId();
			item.setId(id);
			Log.warn("Creating item with no id");
		}
		File file = itemToNative(item);
		bindings.put(id, file);

		saveBindings();
	}

	@Override
	public void updatePackage(Package pack) throws JevernoteException {
		super.updatePackage(pack);

		String id = pack.getId();
		File dir = packageToNative(pack);
		File oldDir = bindings.get(id);
		bindings.put(id, dir);
		
		renamePackageOfItems(oldDir, pack);//FIXME refactorme!

		saveBindings();
	}

	private void renamePackageOfItems(File oldDir, Package pack) {
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

	@Override
	public void updateItem(Item item) throws JevernoteException {
		super.updateItem(item);

		String id = item.getId();
		File file = itemToNative(item);
		bindings.put(id, file);

		saveBindings();
	}

	@Override
	public void removePackage(Package pack) throws JevernoteException {
		super.removePackage(pack);

		String id = pack.getId();
		bindings.remove(id);

		saveBindings();
	}

	@Override
	public void removeItem(Item item) throws JevernoteException {
		super.removeItem(item);

		String id = item.getId();
		bindings.remove(id);

		saveBindings();
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
		File file = new File(basePath, INDEX_FILE_NAME);

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
	public void saveBindings() throws JevernoteException {
		Properties props = toProperties();
		saveProperties(props);

	}

	private Properties toProperties() {
		Properties props = new Properties();

		bindings.forEach((id, file) -> {
			String path = toPath(file);
			if (path != null) {
				props.put(id, path);
			}
		});

		return props;
	}

	private void saveProperties(Properties props) throws JevernoteException {
		File file = new File(basePath, INDEX_FILE_NAME);

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

	private String createId() {
		return "Undefined-identifier-" + System.currentTimeMillis() + "-" + System.nanoTime();
	}

	private String toPath(File file) {
		if (file.isDirectory()) {
			return file.getName();

		} else if (file.isFile()) {
			String dirname = file.getParentFile().getName();
			String filename = file.getName();

			return dirname + File.separatorChar + filename;
		} else if (!file.exists()) {
			return null;
		} else {
			throw new IllegalArgumentException("Invalid file " + file + " type");
		}
	}

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
