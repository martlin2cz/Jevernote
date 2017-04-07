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

public class BaseFileSystemStorage extends CommonStorage<File, File> {

	protected final File basePath;

	public BaseFileSystemStorage(File basePath) {
		super();
		this.basePath = basePath;
	}

	protected List<File> listNativePackages() {
		String[] names = basePath.list();

		return Arrays.stream(names).//
				map((n) -> nameToPackageFile(n)).//
				filter((f) -> f.isFile()). //
				collect(Collectors.toList());
	}

	protected List<File> listNativeItems(Package pack) {
		File dir = packageToNative(pack);
		String[] names = dir.list();

		return Arrays.stream(names).//
				map((n) -> nameToPackageFile(n)).//
				filter((f) -> !f.isDirectory()). // //TODO crash if folder?
				collect(Collectors.toList());

	}

	protected void createPackageNative(Package pack, File nativ) throws IOException {
		boolean succ = nativ.mkdir();
		if (!succ) {
			throw new IOException("Cannot create directory");
		}
	}

	protected void createNativeItem(Item item, File nativ) throws IOException {
		String content = item.getContent();
		writeToFile(nativ, content);
	}

	protected void updatePackageNative(Package pack, File nativ) {
		// TODO Auto-generated method stub

		// TODO FIXME .... infer original pckg(from cfg), compare if renamed (!=
		// title) or what

	}

	protected void updateNativeItem(Item item, File nativ) throws IOException {
		// TODO FIXME .... infer original file (from cfg), compare if renamed
		// (!= title) or just content

		String content = item.getContent();
		// TODO ...
		// TODO if moved to other pack ...
		writeToFile(nativ, content);

	}

	protected void removePackageNative(Package pack, File nativ) throws IOException {
		boolean succ = nativ.delete();
		if (!succ) {
			throw new IOException("Cannot delete directory");
		}
	}

	protected void removeNativeItem(Item item, File nativ) throws IOException {
		boolean succ = nativ.delete();
		if (!succ) {
			throw new IOException("Cannot delete file");
		}
	}

	///////////////////////////////////////////////////////////////////////////

	protected File itemToNative(Item item) {
		String dir = item.getPack().getName();
		String file = item.getName();

		return new File(new File(basePath, dir), file);
		// TOOD FIXME cant make it more beautifull
	}

	protected Item nativeToItem(Package pack, File file) throws IOException {
		String id = null; // TODO FIXME
		String name = file.getName();
		String content = readFile(file);

		Calendar lastModifiedAt = Calendar.getInstance();
		lastModifiedAt.setTime(new Date(file.lastModified()));

		return new Item(pack, id, name, content, lastModifiedAt);
	}

	protected File packageToNative(Package pack) {
		String name = pack.getName();
		return new File(basePath, name);
	}

	protected Package nativeToPackage(File dir) {
		String name = dir.getName();
		String id = null; // TODO FIXME

		return new Package(id, name);
	}

	private File nameToPackageFile(String name) {
		return new File(basePath, name);
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

	protected static File file(File dir, File file) {
		return new File(dir.getAbsolutePath(), file.getName());
	}

}