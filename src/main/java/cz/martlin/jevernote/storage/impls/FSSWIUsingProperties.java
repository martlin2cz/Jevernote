package cz.martlin.jevernote.storage.impls;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cz.martlin.jevernote.dataobj.config.Config;
import cz.martlin.jevernote.misc.FileSystemUtils;
import cz.martlin.jevernote.misc.JevernoteException;

public class FSSWIUsingProperties extends FSstorageWithIndex {

	public static final String INDEX_FILE_NAME = ".index.properties";
	public static final String COMMENT = "Jevernote index file (mapping betweeen names and ids of packages and items)";

	public FSSWIUsingProperties(Config config, File basePath) {
		super(config, basePath);
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

	public boolean existsIndexFile() {
		File file = indexFile(basePath);
		return file.exists();
	}
	///////////////////////////////////////////////////////////////////////////

	protected static Map<String, File> loadBindings(File basePath, File file) throws JevernoteException {
		Properties props = FileSystemUtils.loadProperties(file);
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

	///////////////////////////////////////////////////////////////////////////
	protected static void saveBindings(File file, Map<String, File> bindings) throws JevernoteException {
		Properties props = toProperties(bindings);
		FileSystemUtils.saveProperties(file, props);

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

	///////////////////////////////////////////////////////////////////////////

	protected static File indexFile(File basePath) {
		return new File(basePath, INDEX_FILE_NAME);
	}

}
