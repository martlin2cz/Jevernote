package cz.martlin.jevernote.test;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import cz.martlin.jevernote.dataobj.misc.Config;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.content.base.ContentProcessor;
import cz.martlin.jevernote.storage.content.impls.EvernoteStripingNewliningProcessor;
import cz.martlin.jevernote.storage.impls.EvernoteStorage;
import cz.martlin.jevernote.storage.impls.FSSWIUsingProperties;
import cz.martlin.jevernote.storage.impls.InMemoryStorage;

public class _Testing {

	private static final File base = new File("/home/martin/tmp/jevernote/");

	public static void main(String[] args) {
		// TODO
		testEvernote();
		 testFileSystem();
		// testInMemory();

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
		Config config = new Config();
		FSSWIUsingProperties storage = new FSSWIUsingProperties(config, base);
		try {
			storage.load();
		} catch (JevernoteException e) {
			e.printStackTrace();
			return;
		}

		testStorage(storage);
	}

	private static void testEvernote() {
		final String token = "S=s1:U=93877:E=1629b5a6d92:C=15b43a93f68:P=1cd:A=en-devtoken:V=2:H=e06e49dec02990357292a7928d19624f";

		ContentProcessor proc = new EvernoteStripingNewliningProcessor();

		Config config = new Config();
		config.setAuthToken(token);	//if cfg file does not exist
		EvernoteStorage storage = new EvernoteStorage(config, base, proc);

		try {
			storage.load();
		} catch (JevernoteException e) {
			e.printStackTrace();
			return;
		}

		testStorage(storage);
	}

	private static void testStorage(BaseStorage storage) {
		try {

			// create package
			String name1 = "Můj šestý noteboočík";
			Package pack1 = new Package(null, name1);

			storage.createPackage(pack1);
			System.out.println("Created pack: " + pack1);

			// list packages
			List<Package> packs2 = storage.listPackages();
			System.out.println("List packs: " + packs2);
			Package pack2 = packs2.get(0);

			// create item
			String name3 = "Moje šestá poznámka";
			String content3 = "<span style=\"color:green;\">Ahoj, FAKT, musíš toho udělat ještě hodně!</span><br/>";
			Calendar lastModifiedAt3 = Calendar.getInstance();
			Item item3 = new Item(pack2, null, name3, content3, lastModifiedAt3);

			storage.createItem(item3);
			System.out.println("Created item: " + item3);

			// list items
			List<Item> items4 = storage.listItems(pack2);
			System.out.println("List items: " + items4);

			// update package
			String name5 = "Můj fakt šestý notebočík";
			Package oldPack5 = pack2.copy();
			pack2.setName(name5);
			storage.movePackage(oldPack5, pack2);
			System.out.println("Updated pack: " + pack2);

			// update item
			String name6 = "Moje fakt opravdu šestá a dvětřetin-tá poznámka";
			Item oldItem6 = item3.copy();
			item3.setName(name6);
			storage.moveItem(oldItem6, item3);
			System.out.println("Updated item: " + item3);

			// remove item
			storage.removeItem(item3);
			System.out.println("Removed item.");

			// remove package
			storage.removePackage(pack2);
			System.out.println("Removed package. (not implemented!)");

		} catch (JevernoteException e) {
			e.printStackTrace();
		}
	}

}
