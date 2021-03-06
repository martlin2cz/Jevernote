package cz.martlin.jevernote.storage.impls;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cz.martlin.jevernote.dataobj.misc.Config;
import cz.martlin.jevernote.misc.FileSystemUtils;
import cz.martlin.jevernote.storage.base.BaseFileSystemStorage;

public class FSSWIUsingProperties extends FSstorageWithIndex {

	public static final String INDEX_FILE_NAME = ".index.properties";
	public static final String COMMENT = "Jevernote index file (mapping betweeen names and ids of packages and items)";

	public FSSWIUsingProperties(Config config, File basePath) {
		super(config, basePath);
	}

	@Override
	protected boolean checkBindingsExistence() {
		File indexFile = indexFile(basePath);
		return indexFile.isFile();
	}

	@Override
	protected Map<String, File> initializeBindingsStorage(String noDescNeeded) throws IOException {
		Map<String, File> bindings = new HashMap<>();
		saveBindings(bindings);
		return bindings;
	}

	@Override
	protected Map<String, File> loadBindings() throws IOException {
		File file = indexFile(basePath);
		return loadBindings(basePath, file);
	}

	@Override
	protected void saveBindings(Map<String, File> bindings) throws IOException {
		File file = indexFile(basePath);
		saveBindings(file, bindings);
	}

	public boolean existsIndexFile() {
		File file = indexFile(basePath);
		return file.exists();
	}

	///////////////////////////////////////////////////////////////////////////

	protected static Map<String, File> loadBindings(File basePath, File file) throws IOException {
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
	protected static void saveBindings(File file, Map<String, File> bindings) throws IOException {
		Properties props = toProperties(bindings);
		FileSystemUtils.saveProperties(file, props, COMMENT);

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
