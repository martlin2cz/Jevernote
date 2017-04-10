package cz.martlin.jevernote.impls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNoException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.martlin.jevernote.core.JevernoteException;
import cz.martlin.jevernote.dataobj.Item;
import cz.martlin.jevernote.dataobj.Package;
import cz.martlin.jevernote.misc.Log;

public class FileSystemStorageWithIndexFileTest {
	private static final String PACK0_NAME = "foo";
	private static final String ITEM0_NAME = "Lorem";

	private static File baseDir;

	public FileSystemStorageWithIndexFileTest() {
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	public void testIndexFileItself() throws JevernoteException {
		assertFalse(FileSystemStorageWithIndexFile.hasIndexFile(baseDir));
		try {
			new FileSystemStorageWithIndexFile(baseDir);
			fail("Index file does not exist, should fail");
		} catch (JevernoteException e) {
			// ok
		}

		FileSystemStorageWithIndexFile.createIndexFile(baseDir);

		assertTrue(FileSystemStorageWithIndexFile.hasIndexFile(baseDir));

		try {
			new FileSystemStorageWithIndexFile(baseDir);
		} catch (JevernoteException e) {
			fail("Index file exists, should not fail");
		}
	}

	@Test
	public void testBasicPackages() throws JevernoteException {
		FileSystemStorageWithIndexFile.createIndexFile(baseDir);
		FileSystemStorageWithIndexFile storage = new FileSystemStorageWithIndexFile(baseDir);

		// create package
		final String name = "bar";
		Package pack = createPackageObj(name);
		storage.createPackage(pack);
		File dir1 = dirOfPack(pack);

		assertEquals(dir1, storage.getBindings().get(pack.getId()));
		assertTrue(dir1.isDirectory());

		// insert item
		final String itemName = "Ipsum";
		Item item = createItemObj(pack, itemName, "Something...");
		storage.createItem(item);
		File file2 = fileOfItem(item);

		assertEquals(file2, storage.getBindings().get(item.getId()));
		assertTrue(file2.isFile());

		// rename package
		final String newName = "baaar";
		pack.setName(newName);
		storage.updatePackage(pack);
		File dir3 = dirOfPack(pack);
		File file3 = fileOfItem(item);

		assertEquals(dir3, storage.getBindings().get(pack.getId()));
		assertTrue(dir3.isDirectory());
		assertFalse(dir1.exists());

		assertTrue(file3.isFile());
		assertFalse(file2.exists());

		// remove item (package must be empty while removing)
		storage.removeItem(item);

		// remove package
		storage.removePackage(pack);

		assertNull(storage.getBindings().get(pack.getId()));
		assertFalse(file2.exists());
		assertFalse(dir1.exists());
	}

	@Test
	public void testBasicItems() throws JevernoteException {
		FileSystemStorageWithIndexFile.createIndexFile(baseDir);
		FileSystemStorageWithIndexFile storage = new FileSystemStorageWithIndexFile(baseDir);

		// create item
		final String packName = "foo";
		final String name = "Ipsum";
		
		Package pack = createPackageObj(packName);
		Item item = createItemObj(pack, name, "Something...");
		storage.createPackage(pack);
		storage.createItem(item);
		File file1 = fileOfItem(item);

		assertEquals(file1, storage.getBindings().get(item.getId()));
		assertTrue(file1.isFile());

		// rename item
		final String newName = "Dolor";
		item.setName(newName);
		storage.updateItem(item);
		File file2 = fileOfItem(item);

		assertEquals(file2, storage.getBindings().get(item.getId()));
		assertTrue(file2.isFile());
		assertFalse(file1.exists());

		// create new package and move item into
		final String newPackName = "bar";
		createPackDir(newPackName);
		final Package newPack = createPackageObj(newPackName);

		item.setPack(newPack);
		storage.updateItem(item);
		File file3 = fileOfItem(item);

		assertEquals(file3, storage.getBindings().get(item.getId()));
		assertTrue(file3.isFile());
		assertFalse(file2.exists());
		assertFalse(file1.exists());

		// remove item
		storage.removeItem(item);

		assertNull(storage.getBindings().get(item.getId()));
		assertFalse(file3.exists());
		assertFalse(file2.exists());
		assertFalse(file1.exists());
	}

	@Test
	public void testPackageCreatedByHand() throws JevernoteException {
		FileSystemStorageWithIndexFile.createIndexFile(baseDir);

		// create package dir by hand (not indexed)
		final String name = "qux";
		createPackDir(name);

		// try to rename it
		FileSystemStorageWithIndexFile storage1 = new FileSystemStorageWithIndexFile(baseDir);
		final String newName = "aux";

		Package pack1 = createPackageObj(newName);
		try {
			storage1.updatePackage(pack1);
			fail("Should fail, package does not exist in index");
		} catch (JevernoteException e) {
			// ok
		}

		// try to delete
		FileSystemStorageWithIndexFile storage2 = new FileSystemStorageWithIndexFile(baseDir);

		Package pack2 = createPackageObj(name);
		try {
			storage2.removePackage(pack2);
			// ok
		} catch (JevernoteException e) {
			fail("Should not fail, removed package can be");
		}

	}

	@Test
	public void testIndexedPackage() throws JevernoteException, IOException {
		FileSystemStorageWithIndexFile.createIndexFile(baseDir);

		// create package
		FileSystemStorageWithIndexFile storage1 = new FileSystemStorageWithIndexFile(baseDir);
		final String name = "aux";

		Package pack1 = createPackageObj(name);
		storage1.createPackage(pack1);

		// rename by hand
		final String newName = "qux";

		File dir2 = dirOfPack(pack1);
		Package newPack2 = createPackageObj(newName);
		File newDir2 = dirOfPack(newPack2);

		Files.move(dir2.toPath(), newDir2.toPath());

		// try to rename it
		FileSystemStorageWithIndexFile storage2 = new FileSystemStorageWithIndexFile(baseDir);
		final String newNewName = "fux";

		Package pack3 = createPackageObj(newNewName);
		try {
			storage2.updatePackage(pack3);
			fail("Should fail, package was renamed by hand and hence it does not exist in index");
		} catch (JevernoteException e) {
			// ok
		}
	}

	@Test
	public void testIndexedItem() throws JevernoteException, IOException {
		FileSystemStorageWithIndexFile.createIndexFile(baseDir);

		// create item
		FileSystemStorageWithIndexFile storage1 = new FileSystemStorageWithIndexFile(baseDir);
		final String packName = "foo";
		final String name = "Lorem";

		Item item1 = createItemObj(packName, name, "Something different...");
		storage1.createPackage(item1.getPack());
		storage1.createItem(item1);

		
		// move by hand
		final String newPackName = "qux";
		final String newName = "Amet";
		
		File oldFile = fileOfItem(item1);
		
		Item item2 = createItemObj(newPackName, newName, "(whatever)");
		File newDir = dirOfPack(item2.getPack());
		File newFile = fileOfItem(item2);
		
		
		Files.createDirectory(newDir.toPath());
		Files.move(oldFile.toPath(), newFile.toPath());
		
		
		// try to update its content
		FileSystemStorageWithIndexFile storage2 = new FileSystemStorageWithIndexFile(baseDir);
		final String newContent = "This is different.";
		Item item3 = createItemObj(newPackName, newName, "(whatever)");
		item3.setContent(newContent);
		
		try {
			storage2.updateItem(item3);
			fail("Should fail, the item has in index different file spec");
		} catch (JevernoteException e) {
			// ok
		}
	}

	///////////////////////////////////////////////////////////////////////////

	@BeforeClass
	public static void setUpBeforeClass() {
		final String dirName = "jevernote" + "-" + FileSystemStorageWithIndexFileTest.class.getSimpleName() + "-";

		try {
			Log.write("Creating working tmp directory...");
			Path path = Files.createTempDirectory(dirName);
			baseDir = path.toFile();
		} catch (Exception e) {
			assumeNoException(e);
			Log.warn("Cannot create temp directory, skipping test "
					+ FileSystemStorageWithIndexFileTest.class.getName());
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws IOException {
		delete(baseDir);

		Log.write("Working tmp directory deleted!");
	}

	@Before
	public void setUp() throws Exception {
		/*
		File dir = createPackDir(PACK0_NAME);
		createItemFile(dir, ITEM0_NAME);
		*/
	}

	@After
	public void tearDown() throws Exception {

		Arrays//
				.stream(baseDir.listFiles()) //
				.forEach((f) -> { //
					try {
						delete(f);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
	}

	///////////////////////////////////////////////////////////////////////////

	private Package createPackageObj(String name) {
		String id = "the-pack-" + name + "-" + System.nanoTime();

		Package pack1 = new Package(id, name);
		return pack1;
	}

	private Item createItemObj(String packName, String name, String content) {
		Package pack = createPackageObj(packName);

		return createItemObj(pack, name, content);
	}

	private Item createItemObj(Package pack, String name, String content) {
		String id = "the-item-[" + pack.getName() + "]-" + name + "-" + System.nanoTime();
		Calendar date = Calendar.getInstance();

		Item item = new Item(pack, id, name, content, date);
		return item;
	}

	private File dirOfPack(Package pack) {
		return new File(baseDir, pack.getName());
	}

	private File fileOfItem(Item item) {
		return new File(new File(baseDir, item.getPack().getName()), item.getName());
	}

	///////////////////////////////////////////////////////////////////////////

	private static File createPackDir(String name) throws JevernoteException {
		File dir = new File(baseDir, name);
		Path dirPath = dir.toPath();
		try {
			Files.createDirectory(dirPath);
		} catch (IOException e) {
			throw new JevernoteException(e);
		}
		return dir;
	}

	private static File createItemFile(File dir, String name) throws JevernoteException {
		File file = new File(dir, name);
		Path filePath = file.toPath();
		try {
			Files.createFile(filePath);
		} catch (IOException e) {
			throw new JevernoteException(e);
		}
		return file;
	}

	private static void delete(File file) throws IOException {
		if (file.isDirectory()) {
			for (File sub : file.listFiles()) {
				delete(sub);
			}
		}

		Files.delete(file.toPath());
	}

}
