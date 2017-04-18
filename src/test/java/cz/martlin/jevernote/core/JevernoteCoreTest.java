package cz.martlin.jevernote.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.misc.ConsoleLoggingConfigurer;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.impls.InMemoryStorage;
import cz.martlin.jevernote.tools.TestingUtils;

public class JevernoteCoreTest {

	private static final String PACK_ID0 = "pack-0";
	private static final String PACK_ID1 = "pack-1";
	private static final String PACK_ID2 = "pack-2";
	private static final String PACK_ID3 = "pack-3";

	private static final String ITEM_ID0 = "item-00";
	private static final String ITEM_ID1 = "item-01";
	private static final String ITEM_ID2 = "item-02";
	private static final String ITEM_ID3 = "item-03";
	private static final String ITEM_ID4 = "item-04";

	///////////////////////////////////////////////////////////////////////////

	public JevernoteCoreTest() {
	}

	// @BeforeClass
	// public static void beforeClass() {
	// }

	///////////////////////////////////////////////////////////////////////////

	@Test
	public void testDefault() throws JevernoteException {
		JevernoteCore core = initializeCore();

		startLogging("Test default:");
		core.pushCmd(false, false);
		stopLogging();

		// printStorages(local, remote);

		List<Package> localPackages = ((InMemoryStorage) core.local).list().getPackages();

		assertTrue(localPackages.contains(TestingUtils.pack(PACK_ID0)));
		assertTrue(localPackages.contains(TestingUtils.pack(PACK_ID1)));
		assertTrue(localPackages.contains(TestingUtils.pack(PACK_ID2)));
		assertFalse(localPackages.contains(TestingUtils.pack(PACK_ID3)));
		assertEquals("pack-to-rename", findPack(localPackages, PACK_ID2).getName());

		List<Package> remotePackages = ((InMemoryStorage) core.remote).list().getPackages();

		assertTrue(remotePackages.contains(TestingUtils.pack(PACK_ID0)));
		assertTrue(remotePackages.contains(TestingUtils.pack(PACK_ID1)));
		assertTrue(remotePackages.contains(TestingUtils.pack(PACK_ID2)));
		assertFalse(remotePackages.contains(TestingUtils.pack(PACK_ID3)));
		assertEquals("pack-to-rename", findPack(remotePackages, PACK_ID2).getName());

		List<Item> localItems = ((InMemoryStorage) core.local).list().getItems();

		assertTrue(localItems.contains(TestingUtils.item(ITEM_ID0)));
		assertFalse(localItems.contains(TestingUtils.item(ITEM_ID1)));
		assertTrue(localItems.contains(TestingUtils.item(ITEM_ID2)));
		assertTrue(localItems.contains(TestingUtils.item(ITEM_ID3)));
		assertTrue(localItems.contains(TestingUtils.item(ITEM_ID4)));
		assertEquals("something #3 UPDATED", findItem(localItems, ITEM_ID3).getContent());
		assertEquals("something #4", findItem(localItems, ITEM_ID4).getContent());

		List<Item> remoteItems = ((InMemoryStorage) core.remote).list().getItems();

		assertTrue(remoteItems.contains(TestingUtils.item(ITEM_ID0)));
		assertFalse(remoteItems.contains(TestingUtils.item(ITEM_ID1)));
		assertTrue(remoteItems.contains(TestingUtils.item(ITEM_ID2)));
		assertTrue(remoteItems.contains(TestingUtils.item(ITEM_ID3)));
		assertTrue(remoteItems.contains(TestingUtils.item(ITEM_ID4)));
		assertEquals("something #3 UPDATED", findItem(remoteItems, ITEM_ID3).getContent());
		assertEquals("something #4 UPDATED", findItem(remoteItems, ITEM_ID4).getContent());

		finishCore(core);
	}

	@Test
	public void testWeak() throws JevernoteException {
		JevernoteCore core = initializeCore();

		startLogging("Test weak:");
		core.pushCmd(true, false);
		stopLogging();

		// printStorages(core);

		List<Package> remotePackages = ((InMemoryStorage) core.remote).list().getPackages();

		assertTrue(remotePackages.contains(TestingUtils.pack(PACK_ID1)));
		assertEquals("pack-to-rename", findPack(remotePackages, PACK_ID2).getName());

		List<Item> remoteItems = ((InMemoryStorage) core.remote).list().getItems();

		assertTrue(remoteItems.contains(TestingUtils.item(ITEM_ID2)));
		assertEquals("something #3", findItem(remoteItems, ITEM_ID3).getContent());
		assertEquals("something #4 UPDATED", findItem(remoteItems, ITEM_ID4).getContent());

		finishCore(core);
	}

	@Test
	public void testForce() throws JevernoteException {
		JevernoteCore core = initializeCore();

		startLogging("Test force:");
		core.pushCmd(false, true);
		stopLogging();

		// printStorages(core);

		List<Package> remotePackages = ((InMemoryStorage) core.remote).list().getPackages();

		assertTrue(remotePackages.contains(TestingUtils.pack(PACK_ID1)));
		assertEquals("pack-to-rename", findPack(remotePackages, PACK_ID2).getName());

		List<Item> remoteItems = ((InMemoryStorage) core.remote).list().getItems();

		assertTrue(remoteItems.contains(TestingUtils.item(ITEM_ID2)));
		assertEquals("something #3 UPDATED", findItem(remoteItems, ITEM_ID3).getContent());
		assertEquals("something #4", findItem(remoteItems, ITEM_ID4).getContent());

		assertEquals(core.local, core.remote);

		finishCore(core);

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
		final Item item2 = new Item(pack0, ITEM_ID2, "item-to-remove", "something #2", TestingUtils.makeTime(2));
		final Item item3 = new Item(pack0, ITEM_ID3, "item-updated-in-local", "something #3 UPDATED",
				TestingUtils.makeTime(17));
		final Item item4 = new Item(pack0, ITEM_ID4, "item-updated-in-remote", "something #4",
				TestingUtils.makeTime(20));

		storage.createPackage(pack0);
		storage.createPackage(pack1);
		storage.createPackage(pack2);

		storage.createItem(item0);
		storage.createItem(item2);
		storage.createItem(item3);
		storage.createItem(item4);

		return storage;
	}

	private InMemoryStorage createRemoteStorage() throws JevernoteException {
		InMemoryStorage storage = new InMemoryStorage();

		final Package pack0 = new Package(PACK_ID0, "pack-to-keep");
		// pack 1 have been removed
		final Package pack2 = new Package(PACK_ID2, "pack-to-rename-RENAMED");
		final Package pack3 = new Package(PACK_ID3, "pack-to-add");

		final Item item0 = new Item(pack0, ITEM_ID0, "item-to-keep", "something #0", TestingUtils.makeTime(0));
		final Item item1 = new Item(pack0, ITEM_ID1, "item-to-remove", "something #2", TestingUtils.makeTime(1));
		// item 2 have been removed
		final Item item3 = new Item(pack0, ITEM_ID3, "item-updated-in-local", "something #3",
				TestingUtils.makeTime(20));
		final Item item4 = new Item(pack0, ITEM_ID4, "item-updated-in-remote", "something #4 UPDATED",
				TestingUtils.makeTime(18));

		storage.createPackage(pack0);
		storage.createPackage(pack2);
		storage.createPackage(pack3);

		storage.createItem(item0);
		storage.createItem(item1);
		storage.createItem(item3);
		storage.createItem(item4);

		return storage;
	}

	///////////////////////////////////////////////////////////////////////////

	private void startLogging(String msg) {
		ConsoleLoggingConfigurer.setTo(true, false);
		System.out.println(msg);
	}

	private void stopLogging() {
		System.out.println();
		ConsoleLoggingConfigurer.setTo(false, false);
	}

	private Package findPack(List<Package> list, String id) {
		return list //
				.stream() //
				.filter((p) -> p.getId().equals(id)) //
				.findAny() //
				.get();
	}

	private Item findItem(List<Item> list, String id) {
		return list //
				.stream() //
				.filter((p) -> p.getId().equals(id)) //
				.findAny() //
				.get();
	}

	
	@SuppressWarnings("unused")
	private void printStorages(JevernoteCore core) {

		((InMemoryStorage) core.local).print(System.out);
		System.out.println();
		((InMemoryStorage) core.remote).print(System.out);
		System.out.println();
	}

	private JevernoteCore initializeCore() throws JevernoteException {
		InMemoryStorage local = createLocalStorage();
		InMemoryStorage remote = createRemoteStorage();

		JevernoteCore core = new JevernoteCore(local, remote, false);

		core.load();

		return core;
	}

	private void finishCore(JevernoteCore core) throws JevernoteException {

		core.store();
	}

}
