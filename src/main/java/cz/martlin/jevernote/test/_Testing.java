package cz.martlin.jevernote.test;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import cz.martlin.jevernote.core.BaseStorage;
import cz.martlin.jevernote.core.JevernoteException;
import cz.martlin.jevernote.dataobj.Item;
import cz.martlin.jevernote.dataobj.Package;
import cz.martlin.jevernote.impls.EvernoteStorage;
import cz.martlin.jevernote.impls.FileSystemStorage;

public class _Testing {

	public static void main(String[] args) {
		// TODO
		 //testEvernote();
		 //testFileSystem();
		testInMemory();

	}

	private static void testInMemory() {
		InMemoryStorage storage = new InMemoryStorage();

		try {
			Package pack1 = new Package("Whatever #1", "foo");
			storage.createPackage(pack1);
			Item item1 = //
					new Item(pack1, "Whatever #2", "Lorem", "Příliš žluťoučký kůň úpěl ďáelské ódy",
							Calendar.getInstance());
			storage.createItem(item1);
		} catch (JevernoteException e) {
			e.printStackTrace();
			return;
		}

		testStorage(storage);

		storage.print(System.out);
	}

	private static void testFileSystem() {
		File base = new File("/xxx/tmp/jevernote/");
		FileSystemStorage storage = new FileSystemStorage(base);

		testStorage(storage);
	}

	private static void testEvernote() {
		final String token = "";

		EvernoteStorage storage;
		try {
			storage = new EvernoteStorage(token);
		} catch (JevernoteException e) {
			e.printStackTrace();
			return;
		}

		testStorage(storage);
	}

	private static void testStorage(BaseStorage storage) {
		try {

			// create package
			String name1 = "Můj třetí noteboočík";
			Package pack1 = new Package(null, name1);
			// storage.createPackage(pack1);
			// System.out.println("Created pack: " + pack1);

			// list packages
			List<Package> packs2 = storage.listPackages();
			System.out.println("List packs: " + packs2);
			Package pack2 = packs2.get(0);
			// create item
			String name3 = "Moje třetí poznámka";
			String content3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
					+ "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">"
					//
					+ "<en-note>" //
					+ "<span style=\"color:green;\">Ahoj, musíš toho udělat ještě hodně!</span><br/>"
					//
					+ "</en-note>"; //
			Calendar lastModifiedAt3 = Calendar.getInstance();
			Item item3 = new Item(pack2, null, name3, content3, lastModifiedAt3);
			storage.createItem(item3);
			System.out.println("Created item: " + item3);

			// list items
			List<Item> items4 = storage.listItems(pack2);
			System.out.println("List items: " + items4);

			// update package
			String name5 = "Můj fakt třetí notebočík";
			pack1.setName(name5);
			storage.updatePackage(pack1);
			System.out.println("Updated pack: " + pack1);

			// update item
			String name6 = "Moje fakt opravdu třetí poznámka";
			item3.setName(name6);
			storage.updateItem(item3);
			System.out.println("Updated item: " + item3);

			// remove item
			storage.removeItem(item3);
			System.out.println("Removed item.");

			// remove package
			storage.removePackage(pack1);
			System.out.println("Removed package. (not implemented!)");

		} catch (JevernoteException e) {
			e.printStackTrace();
		}
	}

}
