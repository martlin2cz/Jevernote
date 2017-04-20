package cz.martlin.jevernote.storage.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.martlin.jevernote.dataobj.misc.Config;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.misc.FileSystemUtils;

public abstract class BaseFileSystemStorage extends CommonStorage<File, File> {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	public static final String BACKUP_DIR_NAME = ".backup";
	public static final String IGNORE_FILE_NAME = ".jevernoteignore";

	protected final File basePath;

	private Set<File> ignores;

	public BaseFileSystemStorage(Config config, File basePath) {
		super(config);
		this.basePath = basePath;
	}

	@Override
	public final boolean doIsInstalled() throws Exception {
		return doIsFSInstalled();
	}

	protected abstract boolean doIsFSInstalled() throws IOException;

	@Override
	protected final void doInstallAndLoad(String installData) throws Exception {
		checkBasePathExistence();
		createBackupDir();

		this.ignores = tryToLoadIgnores();

		doFSInstallAndLoad(installData);
	}

	protected abstract void doFSInstallAndLoad(String installData) throws IOException;

	@Override
	protected final void doLoad() throws Exception {
		checkBasePathExistence();
		this.ignores = tryToLoadIgnores();

		doFSLoad();
	}

	protected abstract void doFSLoad() throws IOException;

	@Override
	protected final void doStore() throws Exception {
		doFSStore();
	}

	protected abstract void doFSStore() throws IOException;

	///////////////////////////////////////////////////////////////////////////

	private void checkBasePathExistence() throws FileNotFoundException {
		if (!basePath.isDirectory()) {
			throw new FileNotFoundException("Base dir does not exist");
		}
	}

	private void createBackupDir() throws IOException {
		File backupDir = backupDir();
		
		if (!backupDir.isDirectory()) {
			Files.createDirectory(backupDir.toPath());
		}
	}

	private Set<File> tryToLoadIgnores() {
		LOG.debug("Trying to load ignore file");

		File ignoreFile = new File(basePath, IGNORE_FILE_NAME);
		if (!(ignoreFile.exists() && ignoreFile.isFile())) {
			LOG.debug("Ignore file does not exist");
			return Collections.emptySet();
		}

		List<String> lines = null;
		try {
			lines = FileSystemUtils.loadLines(ignoreFile);
		} catch (IOException e) {
			LOG.warn("Ingore file could not be loaded", e);
			return Collections.emptySet();
		}

		return lines //
				.stream() //
				.map((f) -> new File(basePath, f)) //
				.collect(Collectors.toSet());
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected List<File> listNativePackages() {
		String[] names = basePath.list();

		return Arrays.stream(names).//
				map((n) -> nameToPackageFile(n)).//
				filter((f) -> f.isDirectory()). //
				filter((f) -> !ignores.contains(f)). //
				filter((f) -> !f.getName().equals(BACKUP_DIR_NAME)). //
				collect(Collectors.toList());
	}

	@Override
	protected List<File> listNativeItems(Package pack) {
		File dir = packageToNative(pack);
		String[] names = dir.list();

		return Arrays.stream(names).//
				map((n) -> nameToItemFile(pack, n)).//
				filter((f) -> f.isFile()). // //TODO crash if folder?
				filter((f) -> !ignores.contains(f)). //
				collect(Collectors.toList()); //

	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected void createPackageNative(Package pack, File dir) throws IOException {
		Files.createDirectory(dir.toPath());
	}

	@Override
	protected void createItemNative(Item item, File file) throws IOException {
		String content = item.getContent();
		FileSystemUtils.writeToFile(content, file);
	}

	protected void movePackageNative(Package oldPack, Package newPack, File oldDir, File newDir) throws IOException {
		if (!oldDir.equals(newDir)) {
			Files.move(oldDir.toPath(), newDir.toPath());
		}
	}

	@Override
	protected void moveItemNative(Item oldItem, Item newItem, File oldFile, File newFile) throws IOException {
		if (!oldFile.equals(newFile)) {
			Files.move(oldFile.toPath(), newFile.toPath());
		}
	}

	protected abstract File findPackageDirById(String id) throws IOException;

	@Override
	protected void updateItemNative(Item item, File file) throws IOException {
		// if (!original.getContent().equals(item.getContent())) {
		String content = item.getContent();
		FileSystemUtils.writeToFile(content, file);
		// }
	}

	protected abstract File findItemFileById(String id) throws IOException;

	@Override
	protected void removePackageNative(Package pack, File dir) throws IOException {
		Files.delete(dir.toPath());
	}

	@Override
	protected void removeItemNative(Item item, File file) throws IOException {
		Files.delete(file.toPath());
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected void backupPackageNative(Package pack, File nativ) throws Exception {
		File backupDir = backupDir();
		File backupFile = packageToBackup(backupDir, pack);

		Files.copy(nativ.toPath(), backupFile.toPath());
	}

	@Override
	protected void backupItemNative(Item item, File nativ) throws Exception {
		File backupDir = backupDir();
		File backupFile = itemToBackup(backupDir, item);

		Files.copy(nativ.toPath(), backupFile.toPath());
	}

	private File packageToBackup(File backupDir, Package pack) {
		String timestamp = timestamp();
		String dirName = pack.getName() + "-" + timestamp;
		return new File(backupDir, dirName);
	}

	private File itemToBackup(File backupDir, Item item) {
		String timestamp = timestamp();
		String dirName = item.getPack().getName() + "-" + item.getName() + "-" + timestamp;
		return new File(backupDir, dirName);
	}

	private File backupDir() {
		return new File(basePath, BACKUP_DIR_NAME);
	}

	private String timestamp() {
		Calendar cal = Calendar.getInstance();
		String timestamp = cal.getTime().toString();
		return timestamp.replace(' ', '-');
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected File itemToNative(Item item) {
		Package pack = item.getPack();
		String name = item.getName();

		File dir = packageToNative(pack);
		return new File(dir, name);
	}

	@Override
	protected Item nativeToItem(Package pack, File file) throws IOException {
		String id = findIdOfItem(file);
		String name = file.getName();
		String content = FileSystemUtils.readFile(file);

		Calendar lastModifiedAt = Calendar.getInstance();
		lastModifiedAt.setTime(new Date(file.lastModified()));

		return new Item(pack, id, name, content, lastModifiedAt);
	}

	protected abstract String findIdOfItem(File file) throws IOException;

	@Override
	protected File packageToNative(Package pack) {
		String name = pack.getName();
		return new File(basePath, name);
	}

	@Override
	protected Package nativeToPackage(File dir) throws IOException {
		String name = dir.getName();
		String id = findIdOfPack(dir);

		return new Package(id, name);
	}

	protected abstract String findIdOfPack(File dir) throws IOException;

	protected File nameToPackageFile(String name) {
		return new File(basePath, name);
	}

	protected File nameToItemFile(Package pack, String name) {
		return new File(nameToPackageFile(pack.getName()), name);
	}

	///////////////////////////////////////////////////////////////////////////

	protected static String packOrItemToPath(File fileorDir) {
		if (fileorDir.isDirectory()) {
			return fileorDir.getName();

		} else if (fileorDir.isFile()) {
			String dirname = fileorDir.getParentFile().getName();
			String filename = fileorDir.getName();

			return dirname + File.separatorChar + filename;
		} else if (!fileorDir.exists()) {
			return null;
		} else {
			throw new IllegalArgumentException("Invalid file " + fileorDir + " type");
		}
	}

}