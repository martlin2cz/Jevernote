package cz.martlin.jevernote.storage.impls;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import cz.martlin.jevernote.dataobj.misc.Config;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.misc.FileSystemUtils;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.base.StorageRequiringLoad;

public abstract class BaseFileSystemStorage extends StorageRequiringLoad<File, File> {
	public static final String BACKUP_DIR_NAME = ".backup";

	protected final File basePath;

	public BaseFileSystemStorage(Config config, File basePath) {
		super(config);
		this.basePath = basePath;
	}

	@Override
	public void initialize(String noDescNeeded) throws JevernoteException {
		try {
			createBackupDir();
		} catch (IOException e) {
			throw new JevernoteException("Cannot create backup dir", e);
		}
	}

	private void createBackupDir() throws IOException {
		File backupDir = backupDir();
		Files.createDirectory(backupDir.toPath());
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	protected List<File> listNativePackages() {
		String[] names = basePath.list();

		return Arrays.stream(names).//
				map((n) -> nameToPackageFile(n)).//
				filter((f) -> f.isDirectory()). //
				collect(Collectors.toList());
	}

	@Override
	protected List<File> listNativeItems(Package pack) {
		File dir = packageToNative(pack);
		String[] names = dir.list();

		return Arrays.stream(names).//
				map((n) -> nameToItemFile(pack, n)).//
				filter((f) -> !f.isDirectory()). // //TODO crash if folder?
													// //TODO .jevernoteignore
				collect(Collectors.toList());

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