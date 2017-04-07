package cz.martlin.jevernote.impls;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.martlin.jevernote.core.BaseStorage;
import cz.martlin.jevernote.core.JevernoteException;
import cz.martlin.jevernote.dataobj.Item;
import cz.martlin.jevernote.dataobj.Package;

public class FileSystemStorage implements BaseStorage {

	private final File basePath;

	public FileSystemStorage(File basePath) {
		this.basePath = basePath;
	}

	// TODO constructor without basePath (if well tested with one!)

	@Override
	public Map<Package, List<Item>> list() throws JevernoteException {
		Map<Package, List<Item>> result = new HashMap<>();

		List<Package> packages = listPackages();
		for (Package pack : packages) {
			List<Item> items = listItems(pack);
			result.put(pack, items);
		}

		return result;
	}

	@Override
	public List<Package> listPackages() throws JevernoteException {
		List<Package> packages = new ArrayList<>(basePath.list().length);

		for (String subdir : basePath.list()) {
			File file = subdirToFile(subdir);
			if (!file.isDirectory()) {
				continue;
			}

			Package pack = dirToPackage(file);
			packages.add(pack);
		}

		return packages;
	}

	@Override
	public List<Item> listItems(Package pack) throws JevernoteException {
		File dir = packageToDir(pack);
		List<Item> items = new ArrayList<>(dir.list().length);

		for (String fileName : dir.list()) {
			File file = fileNameToFile(dir, fileName);
			if (!file.isFile()) {
				continue;
			}
			Item item;
			try {
				item = fileToItem(pack, file);
			} catch (IOException e) {
				throw new JevernoteException("Cannot read item", e);
			}
			items.add(item);
		}

		return items;
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public void createPackage(Package pack) throws JevernoteException {
		File dir = packageToDir(pack);

		try {
			boolean succ = dir.mkdir();
			if (!succ) {
				throw new IOException("Cannot create directory");
			}
		} catch (Exception e) {
			throw new JevernoteException("Cannot create package", e);
		}
	}

	@Override
	public void createItem(Item item) throws JevernoteException {
		File file = itemToFile(item);

		try {
			String content = item.getContent();
			writeToFile(file, content);
		} catch (Exception e) {
			throw new JevernoteException("Cannot create item", e);
		}
	}

	@Override
	public void updatePackage(Package pack) throws JevernoteException {
		//TODO FIXME .... infer original pckg(from cfg), compare if renamed (!= title) or what
		

	}

	@Override
	public void updateItem(Item item) throws JevernoteException {
		//TODO FIXME .... infer original file (from cfg), compare if renamed (!= title) or just content
		File file = itemToFile(item);

		try {
			String content = item.getContent();
			//TODO ...
			writeToFile(file, content);
		} catch (Exception e) {
			throw new JevernoteException("Cannot update item", e);
		}
	}

	@Override
	public void removePackage(Package pack) throws JevernoteException {
		File dir = packageToDir(pack);

		try {
			boolean succ = dir.delete();
			if (!succ) {
				throw new IOException("Cannot delete directory");
			}
		} catch (Exception e) {
			throw new JevernoteException("Cannot remove package", e);
		}
		
	}

	@Override
	public void removeItem(Item item) throws JevernoteException {
		Package pack = null;	//TODO ... where the fok is the fokin item?
		File file = itemToFile(item);	

		try {
			boolean succ = file.delete();
			if (!succ) {
				throw new IOException("Cannot delete file");
			}
		} catch (Exception e) {
			throw new JevernoteException("Cannot remove item", e);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	private File fileNameToFile(File dir, String fileName) {
		return new File(new File(basePath, dir.getName()), fileName);
		//TODO FIXME smoooth me!!!!!
	}

	private File subdirToFile(String subdir) {
		File file = new File(basePath, subdir);
		return file;
	}

	private Package dirToPackage(File dir) {
		String name = dir.getName();
		String id = null; // TODO FIXME

		return new Package(id, name);
	}

	private File packageToDir(Package pack) {
		String name = pack.getName();
		return new File(basePath, name);
	}

	private Item fileToItem(Package pack, File file) throws IOException {
		String id = null; // TODO FIXME
		String name = file.getName();
		String content = readFile(file);

		Calendar lastModifiedAt = Calendar.getInstance();
		lastModifiedAt.setTime(new Date(file.lastModified()));

		return new Item(pack, id, name, content, lastModifiedAt);
	}

	private File itemToFile(Item item) {
		String dir = item.getPack().getName();
		String file = item.getName();

		return new File(new File(basePath, dir), file);
		// TOOD FIXME cant make it more beautifull
	}

	///////////////////////////////////////////////////////////////////////////

	private void writeToFile(File file, String content) throws IOException {
		Path path = file.toPath();
		byte[] bytes = content.getBytes();
		OpenOption[] options = new OpenOption[0];

		Files.write(path, bytes, options);
	}

	private String readFile(File file) throws IOException {
		Path path = file.toPath();

		byte[] bytes = Files.readAllBytes(path);

		return new String(bytes);

	}

}
