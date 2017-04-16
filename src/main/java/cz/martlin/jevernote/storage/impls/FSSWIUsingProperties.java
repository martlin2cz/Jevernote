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
import java.util.Properties;

import cz.martlin.jevernote.misc.JevernoteException;

public class FSSWIUsingProperties extends FSstorageWithIndex {

	public static final String INDEX_FILE_NAME = ".index.properties";
	private static final String COMMENT = "Jevernote index file (mapping betweeen names and ids of packages and items)";

	public FSSWIUsingProperties(File basePath) {
		super(basePath);
	}

	@Override
	protected Map<String, File> initializeBindingsStorage(String noDescNeeded) throws JevernoteException {
		Map<String, File> bindings = new HashMap<>();
		saveBindings(bindings);
		return bindings;
	}

	@Override
	protected Map<String, File> loadBindings() throws JevernoteException {
		File file = indexFile(basePath);
		return loadBindings(basePath, file);
	}

	@Override
	protected void saveBindings(Map<String, File> bindings) throws JevernoteException {
		File file = indexFile(basePath);
		saveBindings(file, bindings);
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

}
