package cz.martlin.jevernote.diff.core;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import cz.martlin.jevernote.dataobj.cmp.Change;
import cz.martlin.jevernote.dataobj.cmp.StoragesDifference;
import cz.martlin.jevernote.dataobj.storage.Item;
import cz.martlin.jevernote.dataobj.storage.Package;
import cz.martlin.jevernote.misc.JevernoteException;
import cz.martlin.jevernote.storage.impls.InMemoryStorage;

public class StoragesDifferencerTest {

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

	@Test
	public void test() throws JevernoteException {

		InMemoryStorage local = initSource();
		InMemoryStorage remote = initTarget();

		StoragesDifferencer differ = new StoragesDifferencer();
		StoragesDifference diff = differ.compute(local, remote);
		// System.out.println(diff);

		Set<Change<Package>> actualPacks = new HashSet<>(diff.getPackageDifferences());
		Set<Change<Package>> expectedPacks = createExpectedPackChanges();

		// actualPacks.forEach(System.out::println);
		// System.out.println();
		// expectedPacks.forEach(System.out::println);
		// System.out.println();

		assertEquals(expectedPacks, actualPacks);

		Set<Change<Item>> actualItems = new HashSet<>(diff.getItemsDifferences());
		Set<Change<Item>> expectedItems = createExpectedItemsChanges();

		// actualItems.forEach(System.out::println);
		// System.out.println();
		// expectedItems.forEach(System.out::println);
		// System.out.println();

		assertEquals(expectedItems, actualItems);

	}

	///////////////////////////////////////////////////////////////////////////

	private Set<Change<Package>> createExpectedPackChanges() {
		Set<Change<Package>> expectedPacks = new HashSet<>();

		expectedPacks.add(Change.delete(pack(PACK_ID1)));
		expectedPacks.add(Change.rename(pack(PACK_ID2), pack(PACK_ID2)));
		// PACK_ID3 is kept
		expectedPacks.add(Change.create(pack(PACK_ID3)));
		return expectedPacks;
	}

	private Set<Change<Item>> createExpectedItemsChanges() {
		Set<Change<Item>> expectedItems = new HashSet<>();

		// item 0 is kept
		expectedItems.add(Change.create(item(ITEM_ID1)));
		expectedItems.add(Change.rename(item(ITEM_ID2), item(ITEM_ID2)));
		expectedItems.add(Change.update(item(ITEM_ID3), item(ITEM_ID3)));
		expectedItems.add(Change.delete(item(ITEM_ID4)));

		expectedItems.add(Change.rename(item(ITEM_ID5), item(ITEM_ID5)));
		expectedItems.add(Change.rename(item(ITEM_ID6), item(ITEM_ID6)));
		expectedItems.add(Change.rename(item(ITEM_ID7), item(ITEM_ID7)));
		// item 8 is just been moved
		expectedItems.add(Change.rename(item(ITEM_ID9), item(ITEM_ID9)));
		expectedItems.add(Change.rename(item(ITEM_IDa), item(ITEM_IDa)));
		expectedItems.add(Change.update(item(ITEM_IDb), item(ITEM_IDb)));

		expectedItems.add(Change.rename(item(ITEM_IDc), item(ITEM_IDc)));
		expectedItems.add(Change.update(item(ITEM_IDc), item(ITEM_IDc)));
		expectedItems.add(Change.rename(item(ITEM_IDd), item(ITEM_IDd)));
		expectedItems.add(Change.update(item(ITEM_IDd), item(ITEM_IDd)));
		return expectedItems;
	}

	///////////////////////////////////////////////////////////////////////////

	private InMemoryStorage initSource() throws JevernoteException {
		InMemoryStorage storage = new InMemoryStorage();

		final Package pack0 = new Package(PACK_ID0, "pack-to-keep");
		final Package pack1 = new Package(PACK_ID1, "pack-to-remove");
		final Package pack2 = new Package(PACK_ID2, "pack-to-rename");
		// pack 3 will be added

		final Item item0 = new Item(pack0, ITEM_ID0, "item-to-keep", "something #0", makeTime(0));
		// item 1 will be added
		final Item item2 = new Item(pack0, ITEM_ID2, "item-to-rename", "something #2", makeTime(2));
		final Item item3 = new Item(pack0, ITEM_ID3, "item-to-update", "something #3", makeTime(3));
		final Item item4 = new Item(pack0, ITEM_ID4, "item-to-remove", "something #4", makeTime(4));

		final Item item5 = new Item(pack0, ITEM_ID5, "item-to-rename", "something #5", makeTime(5));
		final Item item6 = new Item(pack0, ITEM_ID6, "item-to-move", "something #6", makeTime(6));
		final Item item7 = new Item(pack0, ITEM_ID7, "item-to-move-rename", "something #7", makeTime(7));
		final Item item8 = new Item(pack2, ITEM_ID8, "item-to-have-been-moved", "something #8", makeTime(8));
		final Item item9 = new Item(pack2, ITEM_ID9, "item-to-have-been-moved-and-rename", "something #9", makeTime(9));
		final Item itemA = new Item(pack2, ITEM_IDa, "item-to-have-been-moved-and-move", "something #10", makeTime(10));
		final Item itemB = new Item(pack2, ITEM_IDb, "item-to-have-been-moved-and-update", "something #11",
				makeTime(11));

		final Item itemC = new Item(pack0, ITEM_IDc, "item-to-rename-update", "something #12", makeTime(12));
		final Item itemD = new Item(pack2, ITEM_IDd, "item-to-have-been-moved-rename-and-update", "something #13",
				makeTime(13));

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

	private InMemoryStorage initTarget() throws JevernoteException {
		InMemoryStorage storage = new InMemoryStorage();

		final Package pack0 = new Package(PACK_ID0, "pack-to-keep");
		// pack 1 was removed
		final Package pack2 = new Package(PACK_ID2, "pack-to-rename-RENAMED");
		final Package pack3 = new Package(PACK_ID3, "pack-to-add");

		final Item item0 = new Item(pack0, ITEM_ID0, "item-to-keep", "something #0", makeTime(0));
		final Item item1 = new Item(pack0, ITEM_ID1, "item-to-add", "something #1", makeTime(1));
		final Item item2 = new Item(pack0, ITEM_ID2, "item-to-rename-RENAMED", "something #2", makeTime(2));
		final Item item3 = new Item(pack0, ITEM_ID3, "item-to-update", "Absoluttely different text", makeTime(3));
		// item 4 was removed

		final Item item5 = new Item(pack0, ITEM_ID5, "item-to-rename-RENAMED", "something #5", makeTime(5));
		final Item item6 = new Item(pack3, ITEM_ID6, "item-to-move", "something #6", makeTime(6));
		final Item item7 = new Item(pack3, ITEM_ID7, "item-to-move-rename-RENAMED", "something #7", makeTime(7));
		final Item item8 = new Item(pack2, ITEM_ID8, "item-to-have-been-moved", "something #8", makeTime(8));
		final Item item9 = new Item(pack2, ITEM_ID9, "item-to-have-been-moved-and-rename-RENAMED", "something #9",
				makeTime(9));
		final Item itemA = new Item(pack3, ITEM_IDa, "item-to-have-been-moved-and-move", "something #10", makeTime(10));
		final Item itemB = new Item(pack2, ITEM_IDb, "item-to-have-been-moved-and-update", "Totally different content.",
				makeTime(11));

		final Item itemC = new Item(pack0, ITEM_IDc, "item-to-rename-update-RENAMED", "Whatever you want to be here!",
				makeTime(12));
		final Item itemD = new Item(pack2, ITEM_IDd, "item-to-have-been-moved-rename-and-update-RENAMED",
				"Everything you ...", makeTime(13));

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

	///////////////////////////////////////////////////////////////////////////

	private Package pack(String id) {
		return new Package(id, "whatever...");
	}

	private Item item(String id) {
		return new Item(pack("xxx"), id, "whatever", "Something ##", makeTime(0));
	}

	private Calendar makeTime(int minsAgo) {
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) - minsAgo);

		return cal;

	}

}
