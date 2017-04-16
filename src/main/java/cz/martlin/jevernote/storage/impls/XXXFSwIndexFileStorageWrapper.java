package cz.martlin.jevernote.storage.impls;

import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.base.WrappingStorage;

@Deprecated
public class XXXFSwIndexFileStorageWrapper extends WrappingStorage {

	public static final String INDEX_FILE_NAME = ".index.properties";
	private static final String COMMENT = "Jevernote index file (mapping betweeen names and ids of packages and items)";

	private final File basePath;

	private XXXFileSystemStorageWithIndexFile wrapped;

	public XXXFSwIndexFileStorageWrapper(File basePath) {
		super();
		this.basePath = basePath;
	}

	@Override
	public BaseStorage getWrapped() {
		if (wrapped == null) {
			throw new IllegalStateException("Not yet initialized");
		}

		return wrapped;
	}

	public Map<String, File> getBindings() {
		return wrapped.getBindings();
	}

	///////////////////////////////////////////////////////////////////////////

	public void install() throws JevernoteException {
		createIndexFile(basePath);
	}

	@Override
	public void initialize() throws JevernoteException {
		File file = indexFile(basePath);
		Map<String, File> bindings = loadBindings(basePath, file);

		wrapped = new XXXFileSystemStorageWithIndexFile(basePath, bindings);
	}

	@Override
	public void finish() throws JevernoteException {
		if (wrapped.isChanged()) {
			Map<String, File> bindings = wrapped.getBindings();
			File file = indexFile(basePath);
			saveBindings(file, bindings);
		}
	}

	public void uninstall() throws JevernoteException {
		removeIndexFile(basePath);
	}

	public boolean hasIndexFile() {
		return hasIndexFile(basePath);
	}

	///////////////////////////////////////////////////////////////////////////

	protected static Map<String, File> loadBindings(File basePath, File file) throws JevernoteException {
		Properties props = loadProperties(file);
		Map<String, File> map = toMap(basePath, props);

		return map;
	}

	private static Map<String, File> toMap(File basePath, Properties props) {
		Map<String, File> result = new HashMap<>(props.size());

		props.forEach((k, v) -> {
			String id = (String) k;
			String path = (String) v;

			result.put(id, new File(basePath, path));
		});

		return result;
	}

	private static Properties loadProperties(File file) throws JevernoteException {

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
	protected static void saveBindings(File file, Map<String, File> bindings) throws JevernoteException {
		Properties props = toProperties(bindings);
		saveProperties(file, props);

	}

	private static Properties toProperties(Map<String, File> bindings) {
		Properties props = new Properties();

		bindings.forEach((id, file) -> {
			String path = BaseFileSystemStorage.packOrItemToPath(file);
			if (path != null) {
				props.put(id, path);
			}
		});

		return props;
	}

	private static void saveProperties(File file, Properties props) throws JevernoteException {
		

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

	public static boolean hasIndexFile(File basePath) {
		File file = indexFile(basePath);
		
		return file.exists() && file.isFile();
	}

	public static void createIndexFile(File basePath) throws JevernoteException {
		File file = indexFile(basePath);
		Properties props = new Properties();
		
		try {
			saveProperties(file, props);
		} catch (JevernoteException e) {
			throw new JevernoteException("Cannot create file", e);
		}
	}

	private static void removeIndexFile(File basePath) throws JevernoteException {
		File file = indexFile(basePath);

		try {
			Files.delete(file.toPath());
		} catch (IOException e) {
			throw new JevernoteException("Cannot create file", e);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	protected static File indexFile(File basePath) {
		return new File(basePath, INDEX_FILE_NAME);
	}

	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// ignore, or not?
			}
		}
	}

	///////////////////////////////////////////////////////////////////////////

}
