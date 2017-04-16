package cz.martlin.jevernote.app;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.martlin.jevernote.core.base.BaseJevernoteCore;
import cz.martlin.jevernote.core.impl.XXXXSimpleJevernoteCore;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.impls.InMemoryStorage;
import cz.martlin.jevernote.tools.TestingUtils;

public class BaseJevernoteCoreTest {

	private static final String PACK_ID0 = "pack-0";
	private static final String PACK_ID1 = "pack-1";
	private static final String PACK_ID2 = "pack-2";
	private static final String PACK_ID3 = "pack-3";

	private static final String ITEM_ID0 = "item-00";
	private static final String ITEM_ID1 = "item-01";
	private static final String ITEM_ID2 = "item-02";
	private static final String ITEM_ID3 = "item-03";
	private static final String ITEM_ID4 = "item-04";
	private static final String ITEM_ID5 = "item-05";
	private static final String ITEM_ID6 = "item-06";
	private static final String ITEM_ID7 = "item-07";
	private static final String ITEM_ID8 = "item-08";
	private static final String ITEM_ID9 = "item-09";
	private static final String ITEM_IDa = "item-10";
	private static final String ITEM_IDb = "item-11";
	private static final String ITEM_IDc = "item-12";
	private static final String ITEM_IDd = "item-13";

	///////////////////////////////////////////////////////////////////////////

	public BaseJevernoteCoreTest() {
	}
	
	@BeforeClass
	public static void beforeClass() {
		ConsoleLoggingConfigurer.setTo(true, true);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	public void testDefault() throws JevernoteException {
		InMemoryStorage local = createLocalStorage();
		InMemoryStorage remote = createRemoteStorage();

		BaseJevernoteCore core = new XXXXSimpleJevernoteCore(local, remote);

		core.pushCmd(false, false);

		local.print(System.out);
		System.out.println();
		remote.print(System.out);
		System.out.println();

		// TODO
	}

	@Test
	public void testWeak() throws JevernoteException {
		InMemoryStorage local = createLocalStorage();
		InMemoryStorage remote = createRemoteStorage();

		BaseJevernoteCore core = new XXXXSimpleJevernoteCore(local, remote);

		core.pushCmd(true, false);

		local.print(System.out);
		System.out.println();
		remote.print(System.out);
		System.out.println();

		// TODO
	}

	@Test
	public void testForce() throws JevernoteException {
		InMemoryStorage local = createLocalStorage();
		InMemoryStorage remote = createRemoteStorage();

		BaseJevernoteCore core = new XXXXSimpleJevernoteCore(local, remote);

		core.pushCmd(false, true);

		local.print(System.out);
		System.out.println();
		remote.print(System.out);
		System.out.println();

		// TODO
		
		//FIXME assertEquals(local, remote);
	}

	///////////////////////////////////////////////////////////////////////////

	private static InMemoryStorage createLocalStorage() throws JevernoteException {
		InMemoryStorage storage = new InMemoryStorage();

		final Package pack0 = new Package(PACK_ID0, "pack-to-keep");
		final Package pack1 = new Package(PACK_ID1, "pack-to-remove");
		final Package pack2 = new Package(PACK_ID2, "pack-to-rename");
		// pack 3 will be added

		final Item item0 = new Item(pack0, ITEM_ID0, "item-to-keep", "something #0", TestingUtils.makeTime(0));
		// item 1 will be added
		final Item item2 = new Item(pack0, ITEM_ID2, "item-to-rename", "something #2", TestingUtils.makeTime(2));
		final Item item3 = new Item(pack0, ITEM_ID3, "item-to-update", "something #3", TestingUtils.makeTime(3));
		final Item item4 = new Item(pack0, ITEM_ID4, "item-to-remove", "something #4", TestingUtils.makeTime(4));

		final Item item5 = new Item(pack0, ITEM_ID5, "item-to-rename", "something #5", TestingUtils.makeTime(5));
		final Item item6 = new Item(pack0, ITEM_ID6, "item-to-move", "something #6", TestingUtils.makeTime(6));
		final Item item7 = new Item(pack0, ITEM_ID7, "item-to-move-rename", "something #7", TestingUtils.makeTime(7));
		final Item item8 = new Item(pack2, ITEM_ID8, "item-to-have-been-moved", "something #8",
				TestingUtils.makeTime(8));
		final Item item9 = new Item(pack2, ITEM_ID9, "item-to-have-been-moved-and-rename", "something #9",
				TestingUtils.makeTime(9));
		final Item itemA = new Item(pack2, ITEM_IDa, "item-to-have-been-moved-and-move", "something #10",
				TestingUtils.makeTime(10));
		final Item itemB = new Item(pack2, ITEM_IDb, "item-to-have-been-moved-and-update", "something #11",
				TestingUtils.makeTime(11));

		final Item itemC = new Item(pack0, ITEM_IDc, "item-to-rename-update", "something #12",
				TestingUtils.makeTime(12));
		final Item itemD = new Item(pack2, ITEM_IDd, "item-to-have-been-moved-rename-and-update", "something #13",
				TestingUtils.makeTime(13));

		storage.createPackage(pack1);
		storage.createPackage(pack2);
		storage.createPackage(pack0);
		storage.createItem(item0);
		storage.createItem(item2);
		storage.createItem(item3);
		storage.createItem(item4);
		storage.createItem(item5);
		storage.createItem(item6);
		storage.createItem(item7);
		storage.createItem(item8);
		storage.createItem(item9);
		storage.createItem(itemA);
		storage.createItem(itemB);
		storage.createItem(itemC);
		storage.createItem(itemD);

		return storage;
	}

	private InMemoryStorage createRemoteStorage() throws JevernoteException {
		InMemoryStorage storage = new InMemoryStorage();

		final Package pack0 = new Package(PACK_ID0, "pack-to-keep");
		// pack 1 was removed
		final Package pack2 = new Package(PACK_ID2, "pack-to-rename-RENAMED");
		final Package pack3 = new Package(PACK_ID3, "pack-to-add");

		final Item item0 = new Item(pack0, ITEM_ID0, "item-to-keep", "something #0", TestingUtils.makeTime(0));
		final Item item1 = new Item(pack0, ITEM_ID1, "item-to-add", "something #1", TestingUtils.makeTime(1));
		final Item item2 = new Item(pack0, ITEM_ID2, "item-to-rename-RENAMED", "something #2",
				TestingUtils.makeTime(2));
		final Item item3 = new Item(pack0, ITEM_ID3, "item-to-update", "Absoluttely different text",
				TestingUtils.makeTime(3));
		// item 4 was removed

		final Item item5 = new Item(pack0, ITEM_ID5, "item-to-rename-RENAMED", "something #5",
				TestingUtils.makeTime(5));
		final Item item6 = new Item(pack3, ITEM_ID6, "item-to-move", "something #6", TestingUtils.makeTime(6));
		final Item item7 = new Item(pack3, ITEM_ID7, "item-to-move-rename-RENAMED", "something #7",
				TestingUtils.makeTime(7));
		final Item item8 = new Item(pack2, ITEM_ID8, "item-to-have-been-moved", "something #8",
				TestingUtils.makeTime(8));
		final Item item9 = new Item(pack2, ITEM_ID9, "item-to-have-been-moved-and-rename-RENAMED", "something #9",
				TestingUtils.makeTime(9));
		final Item itemA = new Item(pack3, ITEM_IDa, "item-to-have-been-moved-and-move", "something #10",
				TestingUtils.makeTime(10));
		final Item itemB = new Item(pack2, ITEM_IDb, "item-to-have-been-moved-and-update", "Totally different content.",
				TestingUtils.makeTime(11));

		final Item itemC = new Item(pack0, ITEM_IDc, "item-to-rename-update-RENAMED", "Whatever you want to be here!",
				TestingUtils.makeTime(12));
		final Item itemD = new Item(pack2, ITEM_IDd, "item-to-have-been-moved-rename-and-update-RENAMED",
				"Everything you ...", TestingUtils.makeTime(13));

		storage.createPackage(pack0);
		storage.createPackage(pack2);
		storage.createPackage(pack3);
		storage.createItem(item0);
		storage.createItem(item1);
		storage.createItem(item2);
		storage.createItem(item3);
		storage.createItem(item5);
		storage.createItem(item6);
		storage.createItem(item7);
		storage.createItem(item8);
		storage.createItem(item9);
		storage.createItem(itemA);
		storage.createItem(itemB);
		storage.createItem(itemC);
		storage.createItem(itemD);

		return storage;
	}

}
