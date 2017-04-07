package cz.martlin.jevernote.impls;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import cz.martlin.jevernote.dataobj.Item;
import cz.martlin.jevernote.dataobj.Package;

public abstract class BaseFileSystemStorage extends CommonStorage<File, File> {

	protected final File basePath;
	
	public BaseFileSystemStorage(File basePath) {
		super();
		this.basePath = basePath;
	}

	protected List<File> listNativePackages() {
		String[] names = basePath.list();

		return Arrays.stream(names).//
				map((n) -> nameToPackageFile(n)).//
				filter((f) -> f.isDirectory()). //
				collect(Collectors.toList());
	}

	protected List<File> listNativeItems(Package pack) {
		File dir = packageToNative(pack);
		String[] names = dir.list();

		return Arrays.stream(names).//
				map((n) -> nameToItemFile(pack, n)).//
				filter((f) -> !f.isDirectory()). // //TODO crash if folder?
				collect(Collectors.toList());

	}


	protected void createPackageNative(Package pack, File dir) throws IOException {
		Files.createDirectory(dir.toPath());
	}

	protected void createNativeItem(Item item, File nativ) throws IOException {
		String content = item.getContent();
		writeToFile(nativ, content);
	}

	protected void updatePackageNative(Package pack, File dir) throws IOException {
		String id = pack.getId();
		File originalDir = findPackageDirById(id);
		// TODO if not found

		if (!originalDir.equals(dir)) {
			Files.move(originalDir.toPath(), dir.toPath());
		}

	}
/*
	protected Package findPackageById(String id) throws IOException {
		File dir = findPackageDirById(id);
		return nativeToPackage(dir);
	}
*/
	protected abstract File findPackageDirById(String id) throws IOException;

	protected void updateNativeItem(Item item, File file) throws IOException {
		String id = item.getId();
		File originalFile = findItemFileById(id);
		// TODO if not found

		if (!originalFile.equals(file)) {
			Files.move(originalFile.toPath(), file.toPath());
		}

	
		//if (!original.getContent().equals(item.getContent())) {
			String content = item.getContent();
			writeToFile(file, content);
		//}
	}
/*
	protected Item findItemById(Package pack, String id) throws IOException {
		File file = findItemFileById(id);
		return nativeToItem(pack, file);
	}
*/
	protected abstract File findItemFileById(String id) throws IOException;

	protected void removePackageNative(Package pack, File dir) throws IOException {
		Files.delete(dir.toPath());
	}

	protected void removeNativeItem(Item item, File file) throws IOException {
		Files.delete(file.toPath());
	}

	///////////////////////////////////////////////////////////////////////////

	protected File itemToNative(Item item) {
		Package pack = item.getPack();
		String name = item.getName();

		File dir = packageToNative(pack);
		return new File(dir, name);
	}

	protected Item nativeToItem(Package pack, File file) throws IOException {
		String id = findIdOfItem(file);
		String name = file.getName();
		String content = readFile(file);

		Calendar lastModifiedAt = Calendar.getInstance();
		lastModifiedAt.setTime(new Date(file.lastModified()));

		return new Item(pack, id, name, content, lastModifiedAt);
	}

	protected abstract String findIdOfItem(File file) throws IOException;

	protected File packageToNative(Package pack) {
		String name = pack.getName();
		return new File(basePath, name);
	}

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

	protected static void writeToFile(File file, String content) throws IOException {
		Path path = file.toPath();
		byte[] bytes = content.getBytes();
		OpenOption[] options = new OpenOption[0];

		Files.write(path, bytes, options);
	}

	protected static String readFile(File file) throws IOException {
		Path path = file.toPath();

		byte[] bytes = Files.readAllBytes(path);

		return new String(bytes);

	}

}