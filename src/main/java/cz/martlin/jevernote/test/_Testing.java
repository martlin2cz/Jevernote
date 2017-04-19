package cz.martlin.jevernote.test;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import cz.martlin.jevernote.app.Main;
import cz.martlin.jevernote.core.JevernoteCore;
import cz.martlin.jevernote.dataobj.misc.Config;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.misc.ConsoleLoggingConfigurer;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.base.BaseStorage;
import cz.martlin.jevernote.storage.base.StorageRequiringLoad;
import cz.martlin.jevernote.storage.content.base.ContentProcessor;
import cz.martlin.jevernote.storage.content.impls.EvernoteStrippingNewliningProcessor;
import cz.martlin.jevernote.storage.impls.EvernoteStorage;
import cz.martlin.jevernote.storage.impls.FSSWIUsingProperties;
import cz.martlin.jevernote.storage.impls.InMemoryStorage;

public class _Testing {

	private static final File base = new File("/home/martin/tmp/jevernote2/");

	public static void main(String[] args) {
		// TODO
		testMain();
		// testCore();

		// testEvernote();
		// testFileSystem();
		// testInMemory();

	}

	private static void testMain() {
		
//		String[] args1 = new String[] { //
//				"--base-dir", base.getAbsolutePath(), "--debug", "init",  "\"S=s1:U=93877:E=1629b5a6d92:C=15b43a93f68:P=1cd:A=en-devtoken:V=2:H=e06e49dec02990357292a7928d19624f\""};//
//		Main.main(args1);

		String[] args3 = new String[] { //
				"--base-dir", base.getAbsolutePath(), "--debug", "status" };//
		Main.main(args3);

		
		String[] args2 = new String[] { //
				"--base-dir", base.getAbsolutePath(), "--debug", "--dry-run", "push" };//
		Main.main(args2);
	}

	private static void testCore() {

		Config config = new Config();

		StorageRequiringLoad local = new InMemoryStorage(config);
		StorageRequiringLoad remote = new FSSWIUsingProperties(config, base);

		boolean save = true;
		boolean interactive = false;
		boolean verbose = true;
		boolean debug = false;
		boolean dry = false;

		ConsoleLoggingConfigurer.setTo(verbose, debug);
		JevernoteCore core = new JevernoteCore(local, remote, interactive, save, dry);
		try {
			core.load();

			// local.createPackage(new Package("id1", "boom"));

			core.pushCmd(false, true);
			// other commands here

			core.store();
		} catch (JevernoteException e) {
			e.printStackTrace();
		}
	}

	private static void testInMemory() {
		Config config = new Config();
		InMemoryStorage storage = new InMemoryStorage(config);

		try {
			storage.checkInstallAndLoad();

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

		testStorage(storage);
	}

	private static void testEvernote() {
		final String token = "S=s1:U=93877:E=1629b5a6d92:C=15b43a93f68:P=1cd:A=en-devtoken:V=2:H=e06e49dec02990357292a7928d19624f";

		ContentProcessor proc = new EvernoteStrippingNewliningProcessor();

		Config config = new Config();
		config.setAuthToken(token); // if cfg file does not exist
		EvernoteStorage storage = new EvernoteStorage(config, base, proc);

		testStorage(storage);
	}

	private static void testStorage(StorageRequiringLoad storage) {
		try {

			storage.checkInstallAndLoad();

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

			storage.checkInstallAndStore();

		} catch (JevernoteException e) {
			e.printStackTrace();
		}
	}

}
